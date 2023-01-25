package com.prgrms.kream.domain.coupon.service;

import static com.prgrms.kream.common.mapper.CouponEventMapper.*;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.prgrms.kream.domain.coupon.dto.request.CouponEventServiceRequest;
import com.prgrms.kream.domain.coupon.dto.response.CouponEventResponse;
import com.prgrms.kream.domain.coupon.model.CouponEvent;
import com.prgrms.kream.domain.coupon.repository.CouponEventRepository;
import com.prgrms.kream.domain.member.model.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponEventService {
	private final CouponEventRepository couponEventRepository;

	public CouponEventResponse registerCouponEvent(CouponEventServiceRequest couponEventServiceRequest) {
		if (checkOverLapApply(couponEventServiceRequest.member())) {
			CouponEvent entity = toCouponEvent(couponEventServiceRequest);
			CouponEvent savedCouponEvent = couponEventRepository.save(entity);
			return toCouponEventResponse(savedCouponEvent);
		} else {
			throw new DuplicateKeyException("쿠폰을 중복으로 받을 수 없습니다.");
		}
	}

	private boolean checkOverLapApply(Member member) {
		return couponEventRepository.findByMember(member).isEmpty();
	}
}