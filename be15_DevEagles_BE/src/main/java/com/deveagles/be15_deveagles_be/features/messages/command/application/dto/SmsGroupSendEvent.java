package com.deveagles.be15_deveagles_be.features.messages.command.application.dto;

import java.util.List;

public record SmsGroupSendEvent(String sender, String content, List<SmsSendUnit> units) {}
