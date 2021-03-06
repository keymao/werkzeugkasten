package com.google.werkzeugkasten.sqlparser.impl;

import static com.google.werkzeugkasten.sqlparser.TokenKind.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.google.werkzeugkasten.sqlparser.Status;
import com.google.werkzeugkasten.sqlparser.TokenKind;

public class ScannerTest {

	@Test
	public void testExecute() {
		String data = "SELECT * FROM HOGE WHERE MOGE = /*?BIND(piro)*/\t10";

		TokenKind[] dataexp = new TokenKind[] {
				// SELECT *
				Text, Text, Text, Text, Text, Text, Whitespace, Text,
				Whitespace,
				// FROM
				Text, Text, Text, Text, Whitespace,
				// HOGE
				Text, Text, Text, Text, Whitespace,
				// WHERE
				Text, Text, Text, Text, Text, Whitespace,
				// MOGE =
				Text, Text, Text, Text, Whitespace, Text, Whitespace,
				// /*?
				BeginSemantic, BeginSemantic, BeginSemantic,
				// BIND(
				Identifier, Identifier, Identifier, Identifier,
				BeginParenthesis,
				// piro)
				Parameter, Parameter, Parameter, Parameter, EndParenthesis,
				// */\t10
				EndSemantic, EndSemantic, Whitespace, Text, Text };

		test(data, dataexp);

	}

	protected void test(String sql, TokenKind[] expected) {
		SqlTokenizeContextImplForUnitTest testdata = new SqlTokenizeContextImplForUnitTest(
				sql);
		Scanner scanner = new Scanner();

		assertEquals(Status.Success, scanner.execute(testdata));
		assertArrayEquals(expected, testdata.getTokens());
	}

	@Test
	public void testExecute2() {
		String data = "SELECT * FROM HOGE WHERE /*? IF(0 < piro.length) {*/"
				+ "\r\nMOGE = 10 \r\n /*? }*/";
		TokenKind[] dataexp = new TokenKind[] {
				// SELECT *
				Text, Text,
				Text,
				Text,
				Text,
				Text,
				Whitespace,
				Text,
				Whitespace,
				// FROM
				Text,
				Text,
				Text,
				Text,
				Whitespace,
				// HOGE
				Text,
				Text,
				Text,
				Text,
				Whitespace,
				// WHERE
				Text,
				Text,
				Text,
				Text,
				Text,
				Whitespace,
				// /*?
				BeginSemantic,
				BeginSemantic,
				BeginSemantic,
				Whitespace,
				// IF(
				Identifier,
				Identifier,
				BeginParenthesis,
				// 0 < piro.length
				Parameter, Whitespace, Parameter, Whitespace, Parameter,
				Parameter, Parameter, Parameter, Parameter, Parameter,
				Parameter, Parameter,
				Parameter,
				Parameter,
				Parameter,
				// ) {
				EndParenthesis,
				Whitespace,
				BeginBrace,
				// */
				EndSemantic,
				EndSemantic,
				// \r\nMOGE = 10 \r\n
				Whitespace, Whitespace, Text, Text, Text, Text, Whitespace,
				Text, Whitespace, Text, Text, Whitespace, Whitespace,
				Whitespace, Whitespace,
				// /*? }*/
				BeginSemantic, BeginSemantic, BeginSemantic, Whitespace,
				EndBrace, EndSemantic, EndSemantic };
		test(data, dataexp);
	}

	@Test
	public void testExecute3() {
		String data = "SELECT * FROM HOGE WHERE /*? IF(0 < piro.size()) {*/"
				+ "\r\nMOGE = 10 \r\n /*? }*/";
		TokenKind[] dataexp = new TokenKind[] {
				// SELECT *
				Text,
				Text,
				Text,
				Text,
				Text,
				Text,
				Whitespace,
				Text,
				Whitespace,
				// FROM
				Text,
				Text,
				Text,
				Text,
				Whitespace,
				// HOGE
				Text,
				Text,
				Text,
				Text,
				Whitespace,
				// WHERE
				Text,
				Text,
				Text,
				Text,
				Text,
				Whitespace,
				// /*?
				BeginSemantic,
				BeginSemantic,
				BeginSemantic,
				Whitespace,
				// IF(
				Identifier,
				Identifier,
				BeginParenthesis,
				// 0 < piro.size()
				Parameter, Whitespace, Parameter, Whitespace, Parameter,
				Parameter, Parameter, Parameter, Parameter, Parameter,
				Parameter,
				Parameter,
				Parameter,
				BeginParenthesis,
				EndParenthesis,
				// ) {
				EndParenthesis,
				Whitespace,
				BeginBrace,
				// */
				EndSemantic,
				EndSemantic,
				// \r\nMOGE = 10 \r\n
				Whitespace, Whitespace, Text, Text, Text, Text, Whitespace,
				Text, Whitespace, Text, Text, Whitespace, Whitespace,
				Whitespace, Whitespace,
				// /*? }*/
				BeginSemantic, BeginSemantic, BeginSemantic, Whitespace,
				EndBrace, EndSemantic, EndSemantic };
		test(data, dataexp);
	}

	@Test
	public void testExecute4() throws Exception {
		String data = " /*?abc((aaa){}*/";
		TokenKind[] dataexp = new TokenKind[] {
				// /*
				Whitespace, BeginSemantic, BeginSemantic, BeginSemantic,
				// abc((
				Identifier, Identifier, Identifier, BeginParenthesis,
				BeginParenthesis,
				// aaa)
				Parameter, Parameter, Parameter, EndParenthesis,
				// {}*/
				BeginBrace, EndBrace, EndSemantic, EndSemantic };
		test(data, dataexp);
	}

	@Test
	public void testExecute5() throws Exception {
		String data = " /* zzzzdada */";
		TokenKind[] dataexp = new TokenKind[] { Whitespace, Text, Text,
				Whitespace, Text, Text, Text, Text, Text, Text, Text, Text,
				Whitespace, Text, Text };
		test(data, dataexp);
	}
}
