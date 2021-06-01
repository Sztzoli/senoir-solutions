package org.example.meetingrooms.repositories.jdbctemplate;

import org.example.meetingrooms.domain.MeetingRoom;
import org.example.meetingrooms.repositories.MeetingRoomRepository;
import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class MariadbMeetingRoomRepository implements MeetingRoomRepository {

    private JdbcTemplate jdbcTemplate;

    public MariadbMeetingRoomRepository() {
        MariaDbDataSource dataSource;
        try {
            dataSource = new MariaDbDataSource();
            dataSource = new MariaDbDataSource();
            dataSource.setUrl("jdbc:mariadb://localhost:3306/employees?useUnicode=true");
            dataSource.setUser("employees");
            dataSource.setPassword("employees");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot create datasource", e);
        }
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public MeetingRoom save(MeetingRoom meetingRoom) {
        String sql = "insert into meeting_rooms(room_name, width, length) values (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, meetingRoom.getName());
                    ps.setInt(2, meetingRoom.getWidth());
                    ps.setInt(3, meetingRoom.getLength());
                    return ps;
                }, keyHolder
        );

        Long id = keyHolder.getKey().longValue();

        return jdbcTemplate
                .queryForObject("select id, room_name, width, length from meeting_rooms where id = ?",
                        new Object[]{id}, MariadbMeetingRoomRepository::mapRowMeetingRoom);
    }

    @Override
    public List<String> findAllSortedByName() {
        String sql = "select room_name from meeting_rooms order by room_name";
        return jdbcTemplate
                .query(sql, (rs, i) -> rs.getString(1));
    }

    @Override
    public List<String> findAllSortedByNameReverse() {
        String sql = "select room_name from meeting_rooms order by room_name desc";
        return jdbcTemplate
                .query(sql, (rs, i) -> rs.getString(1));
    }

    @Override
    public List<String> findEverySecondSortedByName() {

        String sql = """
                SELECT *\s
                FROM (\s
                    SELECT\s
                        @row := @row +1 AS rownum, room_name\s
                    FROM (\s
                        SELECT @row :=0) r, meeting_rooms\s
                    ) ranked\s
                WHERE rownum % 2 = 0
                """;
        return jdbcTemplate
                .query(sql, (rs, i) -> rs.getString("room_name"));
    }

    @Override
    public List<MeetingRoom> findAllSortedByArea() {
        String sql = "select id, room_name, width, length from meeting_rooms order by (width*length) desc";
        return jdbcTemplate.query(sql, MariadbMeetingRoomRepository::mapRowMeetingRoom);
    }

    @Override
    public Optional<MeetingRoom> findByName(String name) {
        String sql = "select id, room_name, width, length from meeting_rooms where room_name = ?";
        MeetingRoom meetingRoom;

        try {
            meetingRoom = jdbcTemplate.queryForObject(sql,
                    new Object[]{name},
                    MariadbMeetingRoomRepository::mapRowMeetingRoom);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        return Optional.of(meetingRoom);
    }

    @Override
    public Optional<MeetingRoom> findByNamePrefix(String name) {
        String sql = "select id, room_name, width, length from meeting_rooms where room_name like ?";
        MeetingRoom meetingRoom;

        try {
            meetingRoom = jdbcTemplate.queryForObject(sql,
                    new Object[]{name+"%"},
                    MariadbMeetingRoomRepository::mapRowMeetingRoom);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        return Optional.of(meetingRoom);
    }

    @Override
    public List<MeetingRoom> findBiggerAreaThen(int area) {
        String sql = "select id, room_name, width, length from meeting_rooms where (width * length) > ?";
       return jdbcTemplate.query(sql,
                    new Object[]{area},
                    MariadbMeetingRoomRepository::mapRowMeetingRoom);

    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from meeting_rooms");
    }

    private static MeetingRoom mapRowMeetingRoom(ResultSet rs, int i) throws SQLException {
        return MeetingRoom.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("room_name"))
                .width(rs.getInt("width"))
                .length(rs.getInt("length"))
                .build();
    }
}
