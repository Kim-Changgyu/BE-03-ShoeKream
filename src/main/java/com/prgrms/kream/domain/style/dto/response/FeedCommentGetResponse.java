package com.prgrms.kream.domain.style.dto.response;

import java.time.LocalDateTime;

public record FeedCommentGetResponse(
		Long id,
		Long memberId,
		String content,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}