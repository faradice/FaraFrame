package com.faradice.faraframe.util;

import java.sql.PreparedStatement;

public interface IDatabaseType {
	void set(PreparedStatement stmt, Object data, int index) throws Exception;
}
