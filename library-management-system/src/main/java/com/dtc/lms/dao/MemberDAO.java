package com.dtc.lms.dao;

import com.dtc.lms.model.Member;
import com.dtc.lms.model.MemberStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data-access contract for the {@code members} table.
 */
public interface MemberDAO {

    /** Inserts a new member. Throws SQLException when the member_id already exists. */
    void insert(Member member) throws SQLException;

    boolean existsById(String memberId) throws SQLException;

    Optional<Member> findById(String memberId) throws SQLException;

    List<Member> findAll() throws SQLException;

    int countAll() throws SQLException;

    int countByStatus(MemberStatus status) throws SQLException;

    /** Used by the automatic suspension rule (Requirement 12). */
    void updateStatus(String memberId, MemberStatus status) throws SQLException;

    /** Used by the "Reset Demo Data" button. */
    void deleteAll() throws SQLException;
}
