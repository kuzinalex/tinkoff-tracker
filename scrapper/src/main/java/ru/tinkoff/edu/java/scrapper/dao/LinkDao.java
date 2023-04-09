package ru.tinkoff.edu.java.scrapper.dao;

import ru.tinkoff.edu.java.scrapper.entity.Link;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

public interface LinkDao {
    long add(URI url);

    int addSubscription(Long tgChatId, Long linkId) throws SQLException;

    int remove(Long tgChatId, Long link);

    Link find(URI url);

    List<Link> findAll(Long tgChatId);
}
