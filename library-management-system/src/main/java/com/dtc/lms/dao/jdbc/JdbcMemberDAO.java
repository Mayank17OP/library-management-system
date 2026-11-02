package com.dtc.lms.dao.jdbc;

import com.dtc.lms.dao.MemberDAO;
import com.dtc.lms.model.Member;
import com.dtc.lms.model.MemberStatus;
import com.dtc.lms.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link MemberDAO} (PreparedStatement only).
 */
public class JdbcMemberDAO implements MemberDAO {

    private static final String SELECT_COLUMNS =
            "SELECT member_id, name, password_hash, status FROM members ";

    @Override
    public void insert(Member member) throws SQLException {
        String sql = "INSERT INTO members (member_id, name, password_hash, status) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, member.getMemberId());
            ps.setString(2, member.getName());
            ps.setString(3, member.getPasswordHash());
            ps.setString(4, member.getStatus().name());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean existsById(String memberId) throws SQLException {
        String sql = "SELECT 1 FROM members WHERE member_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public Optional<Member> findById(String memberId) throws SQLException {
        String sql = SELECT_COLUMNS + "WHERE member_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Member> findAll() throws SQLException {
        String sql = SELECT_COLUMNS + "ORDER BY member_id";
        List<Member> members = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                members.add(mapRow(rs));
            }
        }
        return members;
    }

    @Override
    public int countAll() throws SQLException {
        return singleInt("SELECT COUNT(*) FROM members");
    }

    @Override
    public int countByStatus(MemberStatus status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM members WHERE status = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public void updateStatus(String memberId, MemberStatus status) throws SQLException {
        String sql = "UPDATE members SET status = ? WHERE member_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, memberId);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteAll() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM members")) {
            ps.executeUpdate();
        }
    }

    private int singleInt(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
                rs.getString("member_id"),
                rs.getString("name"),
                rs.getString("password_hash"),
                MemberStatus.valueOf(rs.getString("status")));
    }
}
