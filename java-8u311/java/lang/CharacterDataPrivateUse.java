/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.lang;

/** The CharacterData class encapsulates the large tables found in
    Java.lang.Character. */

/**
 * 私有使用区域是 Unicode 中的一个区域，用于存放各种自定义字符，这些字符没有被分配给任何官方的字符集。
 * 因此，私有使用区域中的字符可以被各种应用程序和系统自由地使用，而不会与其他字符冲突。
 */
class CharacterDataPrivateUse extends CharacterData {

    int getProperties(int ch) {
        return 0;
    }

    int getType(int ch) {
	return (ch & 0xFFFE) == 0xFFFE
	    ? Character.UNASSIGNED
	    : Character.PRIVATE_USE;
    }

    boolean isJavaIdentifierStart(int ch) {
		return false;
    }

    boolean isJavaIdentifierPart(int ch) {
		return false;
    }

    boolean isUnicodeIdentifierStart(int ch) {
		return false;
    }

    boolean isUnicodeIdentifierPart(int ch) {
		return false;
    }

    boolean isIdentifierIgnorable(int ch) {
		return false;
    }

    int toLowerCase(int ch) {
		return ch;
    }

    int toUpperCase(int ch) {
		return ch;
    }

    int toTitleCase(int ch) {
		return ch;
    }

    int digit(int ch, int radix) {
		return -1;
    }

    int getNumericValue(int ch) {
		return -1;
    }

    boolean isWhitespace(int ch) {
		return false;
    }

    byte getDirectionality(int ch) {
	return (ch & 0xFFFE) == 0xFFFE
	    ? Character.DIRECTIONALITY_UNDEFINED
	    : Character.DIRECTIONALITY_LEFT_TO_RIGHT;
    }

    boolean isMirrored(int ch) {
		return false;
    }

    static final CharacterData instance = new CharacterDataPrivateUse();
    private CharacterDataPrivateUse() {};
}

	
