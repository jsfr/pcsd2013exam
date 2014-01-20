package com.acertainsupplychain.utils;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Extends the SimpleFormatter class allowing us to use the level.INFO without
 * timestamps, giving a less cluttered log.
 */
public class SupplyChainFormatter extends SimpleFormatter {
    @Override
    public String format(LogRecord record) {
        if (record.getLevel() == Level.INFO) {
            return record.getMessage() + "\r\n";
        } else {
            return super.format(record);
        }
    }
}
