package com.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import java.util.ArrayList;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageDAO {
	@Resource
	private DataSource ds;

	public ArrayList<Message> getAll() throws SQLException {
		var messagesModel = new ArrayList<Message>();
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM messages");
			) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				messagesModel.add(new Message(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("title"),
						rs.getString("body")));
			}
		}
		return messagesModel;
	}

	public void create(Message mes) throws SQLException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn
						.prepareStatement("INSERT INTO messages(name, title, body) VALUES(?, ?, ?)");
			) {
			pstmt.setString(1, mes.getName());
			pstmt.setString(2, mes.getTitle());
			pstmt.setString(3, mes.getBody());
			pstmt.executeUpdate();
		}
	}

	public void deleteAll() throws SQLException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("DELETE from messages");
			) {
			pstmt.executeUpdate();
		}
	}
}
