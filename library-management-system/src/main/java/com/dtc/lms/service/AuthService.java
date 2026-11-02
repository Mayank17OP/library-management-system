package com.dtc.lms.service;

import com.dtc.lms.dao.MemberDAO;
import com.dtc.lms.dao.jdbc.JdbcMemberDAO;
import com.dtc.lms.model.Member;
import com.dtc.lms.model.MemberStatus;
import com.dtc.lms.util.AppConfig;
import com.dtc.lms.util.PasswordUtil;
import com.dtc.lms.util.Validators;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;

/**
 * Login and registration (Requirements 1, 5 and 6).
 *
 * <p>Security notes:</p>
 * <ul>
 *   <li>Passwords are compared through BCrypt, never as plain text.</li>
 *   <li>The admin account lives in {@link AppConfig} (it is the librarian, not a
 *       library member, so it has no row in the {@code members} table).</li>
 *   <li>The same error message is used for a wrong ID and a wrong password, so
 *       an attacker cannot find out which member IDs exist.</li>
 * </ul>
 */
public class AuthService {

    private final MemberDAO memberDAO;
    private final CirculationService circulationService;

    public AuthService() {
        this(new JdbcMemberDAO(), new CirculationService());
    }

    public AuthService(MemberDAO memberDAO, CirculationService circulationService) {
        this.memberDAO = memberDAO;
        this.circulationService = circulationService;
    }

    /* ======================= 1. ADMIN LOGIN (Requirement 1) ======================= */

    /**
     * Checks the librarian credentials.
     *
     * @return true when the username and the BCrypt password hash match
     */
    public boolean authenticateAdmin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        boolean userMatches = AppConfig.ADMIN_USERNAME.equalsIgnoreCase(username.trim());
        boolean passwordMatches = PasswordUtil.verify(password, AppConfig.ADMIN_PASSWORD_HASH);
        return userMatches && passwordMatches;
    }

    /* ======================= 2. MEMBER LOGIN (Requirement 6) ======================= */

    /**
     * Logs a member in.
     *
     * <p>On every successful login the suspension rule is re-evaluated
     * (Requirement 12), so a defaulter is stopped immediately.</p>
     */
    public Member loginMember(String memberId, String password, LocalDate today)
            throws ServiceException {

        String genericError = "Invalid member ID or password. Please try again.";
        try {
            String cleanId = Validators.requireText(memberId, "Member ID").toUpperCase();
            String cleanPassword = Validators.trim(password);

            Member member = memberDAO.findById(cleanId).orElseThrow(() -> new ServiceException(genericError));
            if (!PasswordUtil.verify(cleanPassword, member.getPasswordHash())) {
                throw new ServiceException(genericError);
            }
            // Requirement 12: block defaulters as soon as they log in.
            circulationService.refreshSuspension(member.getMemberId(), today);
            // Re-read the member so the session sees the up-to-date status.
            return memberDAO.findById(cleanId).orElse(member);

        } catch (ServiceException businessError) {
            throw businessError;
        } catch (IllegalArgumentException blankInput) {
            throw new ServiceException(genericError, blankInput);
        } catch (SQLException ex) {
            throw new ServiceException("Could not contact the database. Please try again.", ex);
        }
    }

    /* ==================== 3. MEMBER REGISTRATION (Requirement 5) ==================== */

    /**
     * Creates a member account. Used both by the librarian (admin panel) and by
     * the self-registration page.
     *
     * @throws ServiceException for a duplicate ID, a blank name or a weak password
     */
    public Member registerMember(String memberId, String name, String password, String confirmPassword)
            throws ServiceException {

        try {
            String cleanId = Validators.requireId(memberId, "Member ID");
            String cleanName = Validators.requireName(name, "Name");
            String cleanPassword = Validators.requirePassword(password);

            if (confirmPassword != null && !cleanPassword.equals(confirmPassword)) {
                throw new ServiceException("The two passwords do not match.");
            }
            if (memberDAO.existsById(cleanId)) {
                throw new ServiceException(
                        "Member ID " + cleanId + " is already registered. Please choose another ID.");
            }
            Member member = new Member(cleanId, cleanName,
                    PasswordUtil.hash(cleanPassword), MemberStatus.ACTIVE);
            memberDAO.insert(member);
            return member;

        } catch (ServiceException businessError) {
            throw businessError;
        } catch (IllegalArgumentException invalidInput) {
            throw new ServiceException(invalidInput.getMessage(), invalidInput);
        } catch (SQLIntegrityConstraintViolationException duplicateKey) {
            throw new ServiceException("That member ID is already registered.", duplicateKey);
        } catch (SQLException ex) {
            throw new ServiceException("Could not register the member. Please try again.", ex);
        }
    }

    /* ============================ 4. READ HELPERS ============================ */

    public List<Member> listMembers() throws ServiceException {
        try {
            return memberDAO.findAll();
        } catch (SQLException ex) {
            throw new ServiceException("Could not load the member list.", ex);
        }
    }

    public Member requireMember(String memberId) throws ServiceException {
        try {
            String cleanId = Validators.requireText(memberId, "Member ID").toUpperCase();
            return memberDAO.findById(cleanId)
                    .orElseThrow(() -> new ServiceException("No member found with ID " + cleanId + "."));
        } catch (ServiceException businessError) {
            throw businessError;
        } catch (IllegalArgumentException invalidInput) {
            throw new ServiceException(invalidInput.getMessage(), invalidInput);
        } catch (SQLException ex) {
            throw new ServiceException("Could not load the member.", ex);
        }
    }
}
