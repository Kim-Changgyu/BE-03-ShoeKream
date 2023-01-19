package com.prgrms.kream.domain.bid.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

public record SellingBidCreateRequest(
		@NotNull(message = "ID는 필수적으로 있어야 합니다")
		Long id,

		@NotNull(message = "판매 입찰을 올린 사람의 ID는 필수적으로 필요합니다")
		Long memberId,

		@NotNull(message = "상품의 사이즈는 필수 입력값입니다")
		Long productOptionId,

		@NotNull(message = "상품의 가격은 필수 입력값입니다")
		int price,
		LocalDateTime validUntil
) {
}