package com.moe.worldbound.client.screen;

import com.moe.worldbound.network.QuestListEntry;
import com.moe.worldbound.network.QuestSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class QuestBookScreen extends Screen {
	private final QuestSyncPayload snapshot;
	private int scrollOffset;

	public QuestBookScreen(QuestSyncPayload snapshot) {
		super(Component.literal("Worldbound Quests"));
		this.snapshot = snapshot;
		this.scrollOffset = Math.max(0, snapshot.activeQuestIndex() - 2);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		extractTransparentBackground(graphics);

		int panelWidth = Math.min(480, width - 24);
		int panelHeight = Math.min(330, height - 24);
		int x = (width - panelWidth) / 2;
		int y = (height - panelHeight) / 2;

		graphics.fillGradient(x, y, x + panelWidth, y + panelHeight, 0xF0141018, 0xF0071B20);
		graphics.outline(x, y, panelWidth, panelHeight, 0xFFE0BC66);
		graphics.outline(x + 3, y + 3, panelWidth - 6, panelHeight - 6, 0x9955E7CC);
		graphics.centeredText(font, Component.literal("Worldbound Quests"), x + panelWidth / 2, y + 10, 0xFFFFF1C4);
		graphics.centeredText(font, Component.literal("Day " + snapshot.campaignDay() + " | Border " + Math.round(snapshot.borderDiameter()) + " blocks | Expanded +" + snapshot.expandedBlocks()), x + panelWidth / 2, y + 22, 0xFF9FEBDD);

		int listX = x + 12;
		int listY = y + 40;
		int rowWidth = panelWidth - 34;
		int rowHeight = 28;
		int visibleRows = visibleRows(panelHeight, rowHeight);
		scrollOffset = clampScroll(scrollOffset, visibleRows);
		int first = scrollOffset;
		for (int row = 0; row < visibleRows; row++) {
			int questIndex = first + row;
			QuestListEntry entry = snapshot.questList().get(questIndex);
			int rowY = listY + row * rowHeight;
			boolean completed = questIndex < snapshot.activeQuestIndex();
			boolean active = questIndex == snapshot.activeQuestIndex();
			boolean unlocked = snapshot.campaignDay() >= entry.unlockDay();
			int fill = active ? 0x80314A42 : completed ? 0x70302516 : 0x60202024;
			int border = active ? 0xFFE0BC66 : completed ? 0xAA55E7CC : 0x7747474F;
			int text = active ? 0xFFFFF1C4 : completed ? 0xFFBCEFE5 : 0xFF8C8C94;
			String state = completed ? "Done" : active && unlocked ? snapshot.progress() + "/" + snapshot.target() : "Day " + entry.unlockDay();
			String chapter = label(entry.chapter());

			graphics.fill(listX, rowY, listX + rowWidth, rowY + rowHeight - 1, fill);
			graphics.outline(listX, rowY, rowWidth, rowHeight - 1, border);
			graphics.text(font, Component.literal(fit(entry.title(), rowWidth - 72)), listX + 5, rowY + 2, text);
			graphics.text(font, Component.literal(state), listX + rowWidth - 58, rowY + 2, text);
			graphics.text(font, Component.literal(chapter), listX + 5, rowY + 14, 0xFFC8A85A);
			graphics.text(font, Component.literal(fit(entry.objective(), rowWidth - 98)), listX + 86, rowY + 14, active && unlocked ? 0xFF9FEBDD : 0xFF7D8588);
		}
		drawScrollBar(graphics, listX + rowWidth + 5, listY, panelHeight - 65, visibleRows);

		graphics.centeredText(font, Component.literal("Showing " + (first + 1) + "-" + Math.min(first + visibleRows, snapshot.questList().size()) + " of " + snapshot.questList().size() + " | Wheel, arrows, PgUp/PgDn | Esc"), x + panelWidth / 2, y + panelHeight - 13, 0xFF9A8F76);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		scrollOffset = clampScroll(scrollOffset - (int) Math.signum(verticalAmount), currentVisibleRows());
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		int visibleRows = currentVisibleRows();
		if (event.key() == GLFW.GLFW_KEY_DOWN) {
			scrollOffset = clampScroll(scrollOffset + 1, visibleRows);
			return true;
		}
		if (event.key() == GLFW.GLFW_KEY_UP) {
			scrollOffset = clampScroll(scrollOffset - 1, visibleRows);
			return true;
		}
		if (event.key() == GLFW.GLFW_KEY_PAGE_DOWN) {
			scrollOffset = clampScroll(scrollOffset + visibleRows, visibleRows);
			return true;
		}
		if (event.key() == GLFW.GLFW_KEY_PAGE_UP) {
			scrollOffset = clampScroll(scrollOffset - visibleRows, visibleRows);
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private int currentVisibleRows() {
		int panelHeight = Math.min(330, height - 24);
		return visibleRows(panelHeight, 28);
	}

	private int visibleRows(int panelHeight, int rowHeight) {
		return Math.max(1, Math.min(snapshot.questList().size(), (panelHeight - 65) / rowHeight));
	}

	private int clampScroll(int offset, int visibleRows) {
		return Math.max(0, Math.min(offset, Math.max(0, snapshot.questList().size() - visibleRows)));
	}

	private void drawScrollBar(GuiGraphicsExtractor graphics, int x, int y, int height, int visibleRows) {
		graphics.fill(x, y, x + 4, y + height, 0x80202024);
		if (snapshot.questList().size() <= visibleRows) {
			graphics.fill(x, y, x + 4, y + height, 0x99E0BC66);
			return;
		}
		int maxScroll = snapshot.questList().size() - visibleRows;
		int thumbHeight = Math.max(18, height * visibleRows / snapshot.questList().size());
		int thumbY = y + (height - thumbHeight) * scrollOffset / Math.max(1, maxScroll);
		graphics.fill(x, thumbY, x + 4, thumbY + thumbHeight, 0xFFE0BC66);
	}

	private String label(String chapter) {
		return switch (chapter) {
			case "SURVIVAL" -> "Survival";
			case "SETTLEMENT" -> "Settlement";
			case "NETHER_PREP" -> "Nether";
			case "BLAZE_AND_PEARLS" -> "Blaze";
			case "DRAGON" -> "Dragon";
			default -> chapter;
		};
	}

	private String fit(String text, int maxWidth) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.font.width(text) <= maxWidth) {
			return text;
		}
		String suffix = "...";
		StringBuilder builder = new StringBuilder(text);
		while (!builder.isEmpty() && minecraft.font.width(builder.toString()) + minecraft.font.width(suffix) > maxWidth) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder + suffix;
	}
}
