package com.richguy.model.command;

import com.zfoo.protocol.util.AssertionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author godotg
 * @version 3.0
 */
public enum CommandEnum {

    clock("clock"),

    yd("yd"),

    jxyd("jxyd"),

    ;

    private static Map<String, CommandEnum> commandMap = new HashMap<>();

    static {
        for (var commandEnum : CommandEnum.values()) {
            var previousValue = commandMap.putIfAbsent(commandEnum.command, commandEnum);
            AssertionUtils.isNull(previousValue, "CommandEnum中不应该含有重复command的枚举类[{}]和[{}]", commandEnum, previousValue);
        }
    }

    private String command;

    CommandEnum(String command) {
        this.command = command;
    }

    public static List<String> allCommands() {
        return new ArrayList<>(commandMap.keySet());
    }

    public String getCommand() {
        return command;
    }

}
