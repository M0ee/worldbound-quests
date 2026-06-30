package com.moe.worldbound.challenge;

import java.util.UUID;

public record ActivePunishment(String id, UUID playerId, long expiresAtGameTime) {
}
