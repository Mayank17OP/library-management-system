package com.dtc.lms.model;

/**
 * A library member (student / faculty).
 *
 * <p>Maps to the {@code members} table:</p>
 * <pre>
 * members(member_id, name, password_hash, status, created_at)
 * </pre>
 * The password itself is never stored - only its BCrypt hash.
 */
public class Member {

    private String memberId;
    private String name;
    private String passwordHash;
    private MemberStatus status = MemberStatus.ACTIVE;

    public Member() {
    }

    public Member(String memberId, String name, MemberStatus status) {
        this.memberId = memberId;
        this.name = name;
        this.status = status;
    }

    public Member(String memberId, String name, String passwordHash, MemberStatus status) {
        this.memberId = memberId;
        this.name = name;
        this.passwordHash = passwordHash;
        this.status = status;
    }

    /** True when the member may borrow books (Requirement 7 and 12). */
    public boolean isActive() {
        return status == MemberStatus.ACTIVE;
    }

    public boolean isSuspended() {
        return status == MemberStatus.SUSPENDED;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Member{" + memberId + ", '" + name + "', status=" + status + '}';
    }
}
