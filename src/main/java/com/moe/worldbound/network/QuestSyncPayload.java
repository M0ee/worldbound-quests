package com.moe.worldbound.network;

import com.moe.worldbound.WorldboundMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record QuestSyncPayload(
		String questId,
		String title,
		String objective,
		String progressText,
		int progress,
		int target,
		double borderDiameter,
		int expandedBlocks,
		int campaignDay,
		boolean started,
		boolean paused,
		boolean hudVisible,
		int activeQuestIndex,
		List<QuestListEntry> questList
) implements CustomPacketPayload {
	public static final Type<QuestSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(WorldboundMod.MOD_ID, "quest_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, QuestSyncPayload> CODEC = new StreamCodec<>() {
		@Override
		public QuestSyncPayload decode(RegistryFriendlyByteBuf buf) {
			List<QuestListEntry> questList = new ArrayList<>();
			int questCount = buf.readVarInt();
			for (int i = 0; i < questCount; i++) {
				questList.add(new QuestListEntry(
						buf.readUtf(),
						buf.readUtf(),
						buf.readUtf(),
						buf.readVarInt(),
						buf.readUtf(),
						buf.readVarInt(),
						buf.readVarInt()
				));
			}
			return new QuestSyncPayload(
					buf.readUtf(),
					buf.readUtf(),
					buf.readUtf(),
					buf.readUtf(),
					buf.readVarInt(),
					buf.readVarInt(),
					buf.readDouble(),
					buf.readVarInt(),
					buf.readVarInt(),
					buf.readBoolean(),
					buf.readBoolean(),
					buf.readBoolean(),
					buf.readVarInt(),
					List.copyOf(questList)
			);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, QuestSyncPayload payload) {
			buf.writeVarInt(payload.questList().size());
			for (QuestListEntry quest : payload.questList()) {
				buf.writeUtf(quest.id());
				buf.writeUtf(quest.title());
				buf.writeUtf(quest.chapter());
				buf.writeVarInt(quest.unlockDay());
				buf.writeUtf(quest.objective());
				buf.writeVarInt(quest.target());
				buf.writeVarInt(quest.reward());
			}
			buf.writeUtf(payload.questId());
			buf.writeUtf(payload.title());
			buf.writeUtf(payload.objective());
			buf.writeUtf(payload.progressText());
			buf.writeVarInt(payload.progress());
			buf.writeVarInt(payload.target());
			buf.writeDouble(payload.borderDiameter());
			buf.writeVarInt(payload.expandedBlocks());
			buf.writeVarInt(payload.campaignDay());
			buf.writeBoolean(payload.started());
			buf.writeBoolean(payload.paused());
			buf.writeBoolean(payload.hudVisible());
			buf.writeVarInt(payload.activeQuestIndex());
		}
	};

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
