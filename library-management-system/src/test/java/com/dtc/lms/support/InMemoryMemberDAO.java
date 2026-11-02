package com.dtc.lms.support;

import com.dtc.lms.dao.MemberDAO;
import com.dtc.lms.model.Member;
import com.dtc.lms.model.MemberStatus;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** In-memory test double for {@link MemberDAO} (unique member_id, like the table). */
public class InMemoryMemberDAO implements MemberDAO {

    private final Map<String, Member> table = new LinkedHashMap<>();

    @Override
    public void insert(Member member) throws SQLException {
        if (table.containsKey(member.getMemberId())) {
            throw new SQLIntegrityConstraintViolationException(
                    "Duplicate entry for key 'PRIMARY' (member_id = " + member.getMemberId() + ")");
        }
        table.put(member.getMemberId(), member);
    }

    @Override
    public boolean existsById(String memberId) {
        return table.containsKey(memberId);
    }

    @Override
    public Optional<Member> findById(String memberId) {
        return Optional.ofNullable(table.get(memberId));
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(table.values());
    }

    @Override
    public int countAll() {
        return table.size();
    }

    @Override
    public int countByStatus(MemberStatus status) {
        return (int) table.values().stream().filter(m -> m.getStatus() == status).count();
    }

    @Override
    public void updateStatus(String memberId, MemberStatus status) {
        Member member = table.get(memberId);
        if (member != null) {
            member.setStatus(status);
        }
    }

    @Override
    public void deleteAll() {
        table.clear();
    }
}
