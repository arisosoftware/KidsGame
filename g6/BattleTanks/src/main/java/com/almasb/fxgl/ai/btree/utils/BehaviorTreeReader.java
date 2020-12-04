/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.btree.utils;

import com.almasb.fxgl.ai.btree.BehaviorTree;

import java.io.*;

/**
 * An abstract event driven {@link BehaviorTree} parser.
 *
 * @author davebaol
 */
public abstract class BehaviorTreeReader {

    private static final String LOG_TAG = "BehaviorTreeReader";

    protected int lineNumber;
    protected boolean reportsComments;

    protected abstract void startLine(int indent);

    protected abstract void startStatement(String name, boolean isSubtreeReference, boolean isGuard);

    protected abstract void attribute(String name, Object value);

    protected abstract void endStatement();

    protected abstract void endLine();

    protected void comment(String text) {
    }

    public BehaviorTreeReader() {
        this(false);
    }

    public BehaviorTreeReader(boolean reportsComments) {
        this.reportsComments = reportsComments;
    }

    /**
     * Parses the given string.
     *
     * @param string the string
     * @throws RuntimeException if the string cannot be successfully parsed.
     */
    public void parse(String string) {
        char[] data = string.toCharArray();
        parse(data, 0, data.length);
    }

    /**
     * Parses the given reader.
     *
     * @param reader the reader
     * @throws RuntimeException if the reader cannot be successfully parsed.
     */
    public void parse(Reader reader) {
        try {
            char[] data = new char[1024];
            int offset = 0;
            while (true) {
                int length = reader.read(data, offset, data.length - offset);
                if (length == -1) break;
                if (length == 0) {
                    char[] newData = new char[data.length * 2];
                    System.arraycopy(data, 0, newData, 0, data.length);
                    data = newData;
                } else
                    offset += length;
            }
            parse(data, 0, offset);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeQuietly(reader);
        }
    }

    private static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Parses the given input stream.
     *
     * @param input the input stream
     * @throws RuntimeException if the input stream cannot be successfully parsed.
     */
    public void parse(InputStream input) {
        try {
            parse(new InputStreamReader(input, "UTF-8"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeQuietly(input);
        }
    }

    /**
     * Parses the given data buffer from the offset up to the specified number of characters.
     *
     * @param data   the buffer
     * @param offset the initial index
     * @param length the specified number of characters to parse.
     * @throws RuntimeException if the buffer cannot be successfully parsed.
     */
    public void parse(char[] data, int offset, int length) {
        int cs, p = offset, pe = length, eof = pe;

        int s = 0;
        int indent = 0;
        int taskIndex = -1;
        boolean isGuard = false;
        boolean isSubtreeRef = false;
        String statementName = null;
        boolean taskProcessed = false;
        boolean needsUnescape = false;
        boolean stringIsUnquoted = false;
        RuntimeException parseRuntimeEx = null;
        String attrName = null;

        lineNumber = 1;

        try {

// line 147 "BehaviorTreeReader.java"
            {
                cs = btree_start;
            }

// line 151 "BehaviorTreeReader.java"
            {
                int _klen;
                int _trans = 0;
                int _acts;
                int _nacts;
                int _keys;
                int _goto_targ = 0;

                _goto:
                while (true) {
                    switch (_goto_targ) {
                        case 0:
                            if (p == pe) {
                                _goto_targ = 4;
                                continue _goto;
                            }
                            if (cs == 0) {
                                _goto_targ = 5;
                                continue _goto;
                            }
                        case 1:
                            _match:
                            do {
                                _keys = _btree_key_offsets[cs];
                                _trans = _btree_index_offsets[cs];
                                _klen = _btree_single_lengths[cs];
                                if (_klen > 0) {
                                    int _lower = _keys;
                                    int _mid;
                                    int _upper = _keys + _klen - 1;
                                    while (true) {
                                        if (_upper < _lower)
                                            break;

                                        _mid = _lower + ((_upper - _lower) >> 1);
                                        if (data[p] < _btree_trans_keys[_mid])
                                            _upper = _mid - 1;
                                        else if (data[p] > _btree_trans_keys[_mid])
                                            _lower = _mid + 1;
                                        else {
                                            _trans += (_mid - _keys);
                                            break _match;
                                        }
                                    }
                                    _keys += _klen;
                                    _trans += _klen;
                                }

                                _klen = _btree_range_lengths[cs];
                                if (_klen > 0) {
                                    int _lower = _keys;
                                    int _mid;
                                    int _upper = _keys + (_klen << 1) - 2;
                                    while (true) {
                                        if (_upper < _lower)
                                            break;

                                        _mid = _lower + (((_upper - _lower) >> 1) & ~1);
                                        if (data[p] < _btree_trans_keys[_mid])
                                            _upper = _mid - 2;
                                        else if (data[p] > _btree_trans_keys[_mid + 1])
                                            _lower = _mid + 2;
                                        else {
                                            _trans += ((_mid - _keys) >> 1);
                                            break _match;
                                        }
                                    }
                                    _trans += _klen;
                                }
                            } while (false);

                            _trans = _btree_indicies[_trans];
                            cs = _btree_trans_targs[_trans];

                            if (_btree_trans_actions[_trans] != 0) {
                                _acts = _btree_trans_actions[_trans];
                                _nacts = (int) _btree_actions[_acts++];
                                while (_nacts-- > 0) {
                                    switch (_btree_actions[_acts++]) {
                                        case 0:
// line 148 "BehaviorTreeReader.rl"
                                        {
                                            String value = new String(data, s, p - s);
                                            s = p;
                                            if (needsUnescape) value = unescape(value);
                                            outer:
                                            if (stringIsUnquoted) {
                                                if (value.equals("true")) {
                                                    attribute(attrName, Boolean.TRUE);
                                                    break outer;
                                                } else if (value.equals("false")) {
                                                    attribute(attrName, Boolean.FALSE);
                                                    break outer;
                                                } else if (value.equals("null")) {
                                                    attribute(attrName, null);
                                                    break outer;
                                                } else { // number
                                                    try {
                                                        if (containsFloatingPointCharacters(value)) {
                                                            attribute(attrName, new Double(value));
                                                            break outer;
                                                        } else {
                                                            attribute(attrName, new Long(value));
                                                            break outer;
                                                        }
                                                    } catch (NumberFormatException nfe) {
                                                        throw new RuntimeException("Attribute value must be a number, a boolean, a string or null");
                                                    }
                                                }
                                            } else {
                                                attribute(attrName, value);
                                            }
                                            stringIsUnquoted = false;
                                        }
                                        break;
                                        case 1:
// line 188 "BehaviorTreeReader.rl"
                                        {
                                            s = p;
                                            needsUnescape = false;
                                            stringIsUnquoted = true;
                                            outer:
                                            while (true) {
                                                switch (data[p]) {
                                                    case '\\':
                                                        needsUnescape = true;
                                                        break;
                                                    case ')':
                                                    case '(':
                                                    case ' ':
                                                    case '\r':
                                                    case '\n':
                                                    case '\t':
                                                        break outer;
                                                }
                                                // if (debug) GdxAI.getLogger().info(LOG_TAG + "unquotedChar (value): '" + data[p] + "'");
                                                p++;
                                                if (p == eof) break;
                                            }
                                            p--;
                                        }
                                        break;
                                        case 2:
// line 213 "BehaviorTreeReader.rl"
                                        {
                                            s = ++p;
                                            needsUnescape = false;
                                            outer:
                                            while (true) {
                                                switch (data[p]) {
                                                    case '\\':
                                                        needsUnescape = true;
                                                        p++;
                                                        break;
                                                    case '"':
                                                        break outer;
                                                }
                                                // if (debug) GdxAI.getLogger().info(LOG_TAG + "quotedChar: '" + data[p] + "'");
                                                p++;
                                                if (p == eof) break;
                                            }
                                            p--;
                                        }
                                        break;
                                        case 3:
// line 233 "BehaviorTreeReader.rl"
                                        {
                                            indent = 0;
                                            taskIndex = -1;
                                            isGuard = false;
                                            isSubtreeRef = false;
                                            statementName = null;
                                            taskProcessed = false;
                                            lineNumber++;
                                        }
                                        break;
                                        case 4:
// line 243 "BehaviorTreeReader.rl"
                                        {
                                            indent++;
                                        }
                                        break;
                                        case 5:
// line 246 "BehaviorTreeReader.rl"
                                        {
                                            if (taskIndex >= 0) {
                                                endStatement(); // Close the last task of the line
                                            }
                                            taskProcessed = true;
                                            if (statementName != null)
                                                endLine();
                                        }
                                        break;
                                        case 6:
// line 255 "BehaviorTreeReader.rl"
                                        {
                                            s = p;
                                        }
                                        break;
                                        case 7:
// line 258 "BehaviorTreeReader.rl"
                                        {
                                            if (reportsComments) {
                                                comment(new String(data, s, p - s));
                                            }
                                        }
                                        break;
                                        case 8:
// line 265 "BehaviorTreeReader.rl"
                                        {
                                            if (taskIndex++ < 0) {
                                                startLine(indent); // First task/guard of the line
                                            } else {
                                                endStatement();  // Close previous task/guard in line
                                            }
                                            statementName = new String(data, s, p - s);
                                            startStatement(statementName, isSubtreeRef, isGuard);  // Start this task/guard
                                            isGuard = false;
                                        }
                                        break;
                                        case 9:
// line 276 "BehaviorTreeReader.rl"
                                        {
                                            attrName = new String(data, s, p - s);
                                        }
                                        break;
                                        case 10:
// line 290 "BehaviorTreeReader.rl"
                                        {
                                            isSubtreeRef = false;
                                        }
                                        break;
                                        case 11:
// line 291 "BehaviorTreeReader.rl"
                                        {
                                            isSubtreeRef = true;
                                        }
                                        break;
                                        case 12:
// line 293 "BehaviorTreeReader.rl"
                                        {
                                            isGuard = true;
                                        }
                                        break;
                                        case 13:
// line 293 "BehaviorTreeReader.rl"
                                        {
                                            isGuard = false;
                                        }
                                        break;
// line 408 "BehaviorTreeReader.java"
                                    }
                                }
                            }

                        case 2:
                            if (cs == 0) {
                                _goto_targ = 5;
                                continue _goto;
                            }
                            if (++p != pe) {
                                _goto_targ = 1;
                                continue _goto;
                            }
                        case 4:
                            if (p == eof) {
                                int __acts = _btree_eof_actions[cs];
                                int __nacts = (int) _btree_actions[__acts++];
                                while (__nacts-- > 0) {
                                    switch (_btree_actions[__acts++]) {
                                        case 0:
// line 148 "BehaviorTreeReader.rl"
                                        {
                                            String value = new String(data, s, p - s);
                                            s = p;
                                            if (needsUnescape) value = unescape(value);
                                            outer:
                                            if (stringIsUnquoted) {
                                                if (value.equals("true")) {
                                                    attribute(attrName, Boolean.TRUE);
                                                    break outer;
                                                } else if (value.equals("false")) {
                                                    attribute(attrName, Boolean.FALSE);
                                                    break outer;
                                                } else if (value.equals("null")) {
                                                    attribute(attrName, null);
                                                    break outer;
                                                } else { // number
                                                    try {
                                                        if (containsFloatingPointCharacters(value)) {
                                                            attribute(attrName, new Double(value));
                                                            break outer;
                                                        } else {
                                                            attribute(attrName, new Long(value));
                                                            break outer;
                                                        }
                                                    } catch (NumberFormatException nfe) {
                                                        throw new RuntimeException("Attribute value must be a number, a boolean, a string or null");
                                                    }
                                                }
                                            } else {
                                                attribute(attrName, value);
                                            }
                                            stringIsUnquoted = false;
                                        }
                                        break;
                                        case 5:
// line 246 "BehaviorTreeReader.rl"
                                        {
                                            if (taskIndex >= 0) {
                                                endStatement(); // Close the last task of the line
                                            }
                                            taskProcessed = true;
                                            if (statementName != null)
                                                endLine();
                                        }
                                        break;
                                        case 6:
// line 255 "BehaviorTreeReader.rl"
                                        {
                                            s = p;
                                        }
                                        break;
                                        case 7:
// line 258 "BehaviorTreeReader.rl"
                                        {
                                            if (reportsComments) {
                                                comment(new String(data, s, p - s));
                                            }
                                        }
                                        break;
                                        case 8:
// line 265 "BehaviorTreeReader.rl"
                                        {
                                            if (taskIndex++ < 0) {
                                                startLine(indent); // First task/guard of the line
                                            } else {
                                                endStatement();  // Close previous task/guard in line
                                            }
                                            statementName = new String(data, s, p - s);
                                            startStatement(statementName, isSubtreeRef, isGuard);  // Start this task/guard
                                            isGuard = false;
                                        }
                                        break;
                                        case 10:
// line 290 "BehaviorTreeReader.rl"
                                        {
                                            isSubtreeRef = false;
                                        }
                                        break;
                                        case 11:
// line 291 "BehaviorTreeReader.rl"
                                        {
                                            isSubtreeRef = true;
                                        }
                                        break;
// line 522 "BehaviorTreeReader.java"
                                    }
                                }
                            }

                        case 5:
                    }
                    break;
                }
            }

// line 301 "BehaviorTreeReader.rl"

        } catch (RuntimeException ex) {
            parseRuntimeEx = ex;
        }

        if (p < pe || (statementName != null && !taskProcessed)) {
            throw new RuntimeException("Error parsing behavior tree on line " + lineNumber + " near: " + new String(data, p, pe - p),
                    parseRuntimeEx);
        } else if (parseRuntimeEx != null) {
            throw new RuntimeException("Error parsing behavior tree: " + new String(data), parseRuntimeEx);
        }
    }


    // line 545 "BehaviorTreeReader.java"
    private static byte[] init__btree_actions_0() {
        return new byte[]{
                0, 1, 0, 1, 1, 1, 2, 1, 3, 1, 4, 1,
                5, 1, 6, 1, 9, 1, 12, 1, 13, 2, 0, 5,
                2, 0, 13, 2, 5, 3, 2, 7, 5, 2, 10, 8,
                2, 11, 8, 3, 0, 5, 3, 3, 6, 7, 5, 3,
                7, 5, 3, 3, 10, 8, 5, 3, 10, 8, 13, 3,
                11, 8, 5, 3, 11, 8, 13, 4, 6, 7, 5, 3,
                4, 10, 8, 5, 3, 4, 11, 8, 5, 3
        };
    }

    private static final byte _btree_actions[] = init__btree_actions_0();


    private static short[] init__btree_key_offsets_0() {
        return new short[]{
                0, 0, 1, 6, 16, 21, 33, 37, 47, 59, 63, 72,
                73, 77, 82, 86, 99, 108, 120, 124, 133, 137, 138, 142,
                146, 151, 155, 167, 172, 174, 176, 189, 194, 208, 218, 223,
                228
        };
    }

    private static final short _btree_key_offsets[] = init__btree_key_offsets_0();


    private static char[] init__btree_trans_keys_0() {
        return new char[]{
                10, 95, 65, 90, 97, 122, 9, 13, 32, 36, 41, 95,
                65, 90, 97, 122, 95, 65, 90, 97, 122, 9, 13, 32,
                41, 63, 95, 48, 57, 65, 90, 97, 122, 9, 13, 32,
                41, 9, 13, 32, 36, 40, 95, 65, 90, 97, 122, 9,
                13, 32, 58, 63, 95, 48, 57, 65, 90, 97, 122, 9,
                13, 32, 58, 9, 10, 13, 32, 34, 35, 58, 40, 41,
                34, 9, 13, 32, 58, 95, 65, 90, 97, 122, 9, 13,
                32, 41, 9, 13, 32, 41, 46, 63, 95, 48, 57, 65,
                90, 97, 122, 9, 13, 32, 41, 95, 65, 90, 97, 122,
                9, 13, 32, 58, 63, 95, 48, 57, 65, 90, 97, 122,
                9, 13, 32, 58, 9, 10, 13, 32, 34, 35, 58, 40,
                41, 9, 13, 32, 41, 34, 9, 13, 32, 41, 9, 13,
                32, 58, 95, 65, 90, 97, 122, 9, 13, 32, 41, 9,
                10, 13, 32, 35, 36, 40, 95, 65, 90, 97, 122, 9,
                10, 13, 32, 35, 10, 13, 10, 13, 9, 10, 13, 32,
                35, 63, 95, 48, 57, 65, 90, 97, 122, 9, 10, 13,
                32, 35, 9, 10, 13, 32, 35, 46, 63, 95, 48, 57,
                65, 90, 97, 122, 9, 10, 13, 32, 35, 95, 65, 90,
                97, 122, 9, 10, 13, 32, 35, 9, 10, 13, 32, 35,
                9, 10, 13, 32, 35, 0
        };
    }

    private static final char _btree_trans_keys[] = init__btree_trans_keys_0();


    private static byte[] init__btree_single_lengths_0() {
        return new byte[]{
                0, 1, 1, 6, 1, 6, 4, 6, 6, 4, 7, 1,
                4, 1, 4, 7, 5, 6, 4, 7, 4, 1, 4, 4,
                1, 4, 8, 5, 2, 2, 7, 5, 8, 6, 5, 5,
                5
        };
    }

    private static final byte _btree_single_lengths[] = init__btree_single_lengths_0();


    private static byte[] init__btree_range_lengths_0() {
        return new byte[]{
                0, 0, 2, 2, 2, 3, 0, 2, 3, 0, 1, 0,
                0, 2, 0, 3, 2, 3, 0, 1, 0, 0, 0, 0,
                2, 0, 2, 0, 0, 0, 3, 0, 3, 2, 0, 0,
                0
        };
    }

    private static final byte _btree_range_lengths[] = init__btree_range_lengths_0();


    private static short[] init__btree_index_offsets_0() {
        return new short[]{
                0, 0, 2, 6, 15, 19, 29, 34, 43, 53, 58, 67,
                69, 74, 78, 83, 94, 102, 112, 117, 126, 131, 133, 138,
                143, 147, 152, 163, 169, 172, 175, 186, 192, 204, 213, 219,
                225
        };
    }

    private static final short _btree_index_offsets[] = init__btree_index_offsets_0();


    private static byte[] init__btree_indicies_0() {
        return new byte[]{
                0, 1, 2, 2, 2, 1, 3, 3, 3, 4, 5, 6,
                6, 6, 1, 7, 7, 7, 1, 8, 8, 8, 9, 11,
                10, 10, 10, 10, 1, 12, 12, 12, 5, 1, 13, 13,
                13, 14, 15, 16, 16, 16, 1, 17, 17, 17, 19, 20,
                18, 18, 18, 18, 1, 21, 21, 21, 22, 1, 22, 1,
                22, 22, 24, 1, 1, 1, 23, 25, 1, 17, 17, 17,
                19, 1, 26, 26, 26, 1, 8, 8, 8, 9, 1, 27,
                27, 27, 28, 29, 31, 30, 30, 30, 30, 1, 32, 32,
                32, 5, 33, 33, 33, 1, 34, 34, 34, 36, 37, 35,
                35, 35, 35, 1, 38, 38, 38, 39, 1, 39, 1, 39,
                39, 41, 1, 1, 1, 40, 42, 42, 42, 43, 1, 44,
                1, 32, 32, 32, 5, 1, 34, 34, 34, 36, 1, 30,
                30, 30, 1, 27, 27, 27, 28, 1, 45, 46, 47, 45,
                48, 14, 15, 16, 16, 16, 1, 47, 46, 47, 47, 48,
                1, 50, 51, 49, 53, 54, 52, 55, 56, 55, 55, 57,
                59, 58, 58, 58, 58, 1, 55, 56, 55, 55, 57, 1,
                60, 61, 60, 60, 62, 63, 64, 26, 26, 26, 26, 1,
                65, 46, 65, 65, 48, 66, 66, 66, 1, 67, 68, 67,
                67, 69, 1, 65, 46, 65, 65, 48, 1, 60, 61, 60,
                60, 62, 1, 0
        };
    }

    private static final byte _btree_indicies[] = init__btree_indicies_0();


    private static byte[] init__btree_trans_targs_0() {
        return new byte[]{
                26, 0, 30, 3, 4, 7, 15, 5, 6, 7, 5, 14,
                6, 7, 2, 3, 32, 9, 8, 10, 12, 9, 10, 34,
                11, 35, 32, 16, 7, 24, 15, 25, 16, 17, 18, 17,
                19, 23, 18, 19, 20, 21, 16, 7, 22, 26, 26, 27,
                28, 29, 26, 1, 29, 26, 1, 27, 26, 28, 30, 31,
                33, 26, 28, 13, 36, 33, 8, 33, 26, 28
        };
    }

    private static final byte _btree_trans_targs[] = init__btree_trans_targs_0();


    private static byte[] init__btree_trans_actions_0() {
        return new byte[]{
                7, 0, 13, 0, 0, 19, 13, 13, 36, 63, 0, 0,
                0, 0, 0, 17, 13, 15, 0, 15, 0, 0, 0, 3,
                5, 1, 0, 33, 55, 0, 0, 0, 0, 13, 15, 0,
                15, 0, 0, 0, 3, 5, 1, 24, 1, 9, 27, 0,
                0, 13, 67, 43, 0, 47, 30, 36, 77, 36, 0, 0,
                33, 72, 33, 0, 0, 0, 13, 1, 39, 1
        };
    }

    private static final byte _btree_trans_actions[] = init__btree_trans_actions_0();


    private static byte[] init__btree_eof_actions_0() {
        return new byte[]{
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 11, 11, 43, 30, 59, 59, 51, 11, 21, 11,
                51
        };
    }

    private static final byte _btree_eof_actions[] = init__btree_eof_actions_0();


    static final int btree_start = 26;
    static final int btree_first_final = 26;
    static final int btree_error = 0;

    static final int btree_en_main = 26;


// line 315 "BehaviorTreeReader.rl"

    private static boolean containsFloatingPointCharacters(String value) {
        for (int i = 0, n = value.length(); i < n; i++) {
            switch (value.charAt(i)) {
                case '.':
                case 'E':
                case 'e':
                    return true;
            }
        }
        return false;
    }

    private static String unescape(String value) {
        int length = value.length();
        StringBuilder buffer = new StringBuilder(length + 16);
        for (int i = 0; i < length; ) {
            char c = value.charAt(i++);
            if (c != '\\') {
                buffer.append(c);
                continue;
            }
            if (i == length) break;
            c = value.charAt(i++);
            if (c == 'u') {
                buffer.append(Character.toChars(Integer.parseInt(value.substring(i, i + 4), 16)));
                i += 4;
                continue;
            }
            switch (c) {
                case '"':
                case '\\':
                case '/':
                    break;
                case 'b':
                    c = '\b';
                    break;
                case 'f':
                    c = '\f';
                    break;
                case 'n':
                    c = '\n';
                    break;
                case 'r':
                    c = '\r';
                    break;
                case 't':
                    c = '\t';
                    break;
                default:
                    throw new RuntimeException("Illegal escaped character: \\" + c);
            }
            buffer.append(c);
        }
        return buffer.toString();
    }
}