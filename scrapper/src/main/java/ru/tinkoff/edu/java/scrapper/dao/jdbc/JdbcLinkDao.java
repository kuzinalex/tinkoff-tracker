package ru.tinkoff.edu.java.scrapper.dao.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.edu.java.scrapper.dao.LinkDao;
import ru.tinkoff.edu.java.scrapper.entity.Link;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Repository
public class JdbcLinkDao implements LinkDao {

	private JdbcTemplate jdbcTemplate;

	private final String SQL_INSERT_LINK = "INSERT INTO link (url) VALUES (?)";
	private final String SQL_INSERT_SUBSCRIPTION = "INSERT INTO subscription  VALUES (?,?)";
	private final String SQL_UPDATED_LINK = "UPDATE link SET updated_at=(?), check_time=NOW() WHERE id=(?)";
	private final String SQL_REMOVE_SUBSCRIPTION = "DELETE FROM subscription WHERE chat_id=(?) AND link_id=(?)";
	private final String SQL_FIND_LINK = "SELECT * FROM link WHERE url=(?)";
	private final String SQL_FIND_ALL_LINKS = "SELECT * FROM link JOIN subscription s on link.id = s.link_id WHERE chat_id=(?)";
	private final String SQL_FIND_OLD_LINKS = "SELECT * FROM link WHERE check_time<=(?)";

	public JdbcLinkDao(JdbcTemplate jdbcTemplate) {

		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional
	@Override
	public long add(URI url) {

		String insertQuery = SQL_INSERT_LINK;
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection
					.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, url.toString());
			return ps;
		}, keyHolder);

		return (long) keyHolder.getKeys().get("id");
	}

	@Transactional
	@Override
	public int addSubscription(Long tgChatId, Long linkId) {

		String insertQuery = SQL_INSERT_SUBSCRIPTION;

		return jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection
					.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, tgChatId);
			ps.setLong(2, linkId);
			return ps;
		});

	}

	@Override
	public int update(Link link) {

		String insertQuery = SQL_UPDATED_LINK;
		return jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection
					.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ps.setTimestamp(1, Timestamp.valueOf(link.getUpdatedAt().toLocalDateTime()));
			ps.setLong(2, link.getId());
			return ps;
		});
	}

	@Transactional
	@Override
	public int remove(Long tgChatId, Long link) {

		return jdbcTemplate.update(SQL_REMOVE_SUBSCRIPTION, ps -> {
			ps.setLong(1, tgChatId);
			ps.setLong(2, link);
		});
	}

	@Transactional
	@Override
	public Link find(URI url) {

		return jdbcTemplate.query(SQL_FIND_LINK, ps -> {
			ps.setString(1, url.toString());
		}, linkRowMapper()).stream().findFirst().orElse(null);
	}

	@Transactional
	@Override
	public List<Link> findAll(Long tgChatId) {

		return jdbcTemplate.query(SQL_FIND_ALL_LINKS, ps -> {
			ps.setLong(1, tgChatId);
		}, linkRowMapper());
	}

	@Transactional
	@Override
	public List<Link> findOld(OffsetDateTime checkTime) {

		return jdbcTemplate.query(SQL_FIND_OLD_LINKS, ps -> {
			ps.setTimestamp(1, Timestamp.valueOf(checkTime.toLocalDateTime()));
		}, linkRowMapper());
	}

	private RowMapper<Link> linkRowMapper() {

		return (rs, rowNum) -> {
			Link link = new Link();
			link.setId(rs.getLong("id"));
			link.setUrl(rs.getString("url"));
			link.setCheckTime(OffsetDateTime.of(rs.getObject("check_time", LocalDateTime.class), ZoneOffset.UTC));
			link.setUpdatedAt(OffsetDateTime.of(rs.getObject("updated_at", LocalDateTime.class), ZoneOffset.UTC));
			return link;
		};
	}
}
