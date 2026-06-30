package com.moe.worldbound.network;

import com.moe.worldbound.WorldboundMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PunishmentSyncPayload(String message) implements CustomPacketPayload {
	public static final Type<PunishmentSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(WorldboundMod.MOD_ID, "punishment_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PunishmentSyncPayload> CODEC = new StreamCodec<>() {
		@Override
		public PunishmentSyncPayload decode(RegistryFriendlyByteBuf buf) {
			return new PunishmentSyncPayload(buf.readUtf());
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, PunishmentSyncPayload payload) {
			buf.writeUtf(payload.message());
		}
	};

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
