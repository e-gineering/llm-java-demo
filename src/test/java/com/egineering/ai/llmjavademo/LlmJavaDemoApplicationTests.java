package com.egineering.ai.llmjavademo;

import org.junit.jupiter.api.Test;

import java.util.StringTokenizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
class LlmJavaDemoApplicationTests {

//	@Test
//	void contextLoads() {
//	}

	@Test
	public void testTokenCount() {
		String testString = """
				Some bunch       of random
				text
				
				that      		\s
				           
				    has      some         \s
				weird 			whitespace
				
				""";
		StringTokenizer tokenizer = new StringTokenizer(testString);

		assertEquals(10, tokenizer.countTokens());
		assertEquals("Some", tokenizer.nextToken());
		assertEquals("bunch", tokenizer.nextToken());
		assertEquals("of", tokenizer.nextToken());
		assertEquals("random", tokenizer.nextToken());
		assertEquals("text", tokenizer.nextToken());
		assertEquals("that", tokenizer.nextToken());
		assertEquals("has", tokenizer.nextToken());
		assertEquals("some", tokenizer.nextToken());
		assertEquals("weird", tokenizer.nextToken());
		assertEquals("whitespace", tokenizer.nextToken());
	}
}
