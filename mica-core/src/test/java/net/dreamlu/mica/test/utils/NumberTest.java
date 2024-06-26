package net.dreamlu.mica.test.utils;

import net.dreamlu.mica.core.utils.NumberUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NumberTest {

	@Test
	void testTo62String1() {
		long ms = 1551320493447L;
		String string = NumberUtil.to62Str(ms);
		Assertions.assertEquals("rjkOH7p", string);
		long l = NumberUtil.form62Str(string);
		Assertions.assertEquals(ms, l);
	}

	@Test
	void testTo62String2() {
		long ms = 1657930102030434306L;
		String string = NumberUtil.to62Str(ms);
		Assertions.assertEquals("1YtkVbn657s", string);
		long l = NumberUtil.form62Str(string);
		Assertions.assertEquals(ms, l);
	}


}
