package com.prgrms.kream.style.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.kream.domain.member.model.Authority;
import com.prgrms.kream.domain.member.model.Member;
import com.prgrms.kream.domain.style.dto.request.LikeFeedServiceRequest;
import com.prgrms.kream.domain.style.dto.request.RegisterFeedServiceRequest;
import com.prgrms.kream.domain.style.dto.request.UpdateFeedServiceRequest;
import com.prgrms.kream.domain.style.dto.response.RegisterFeedServiceResponse;
import com.prgrms.kream.domain.style.dto.response.UpdateFeedServiceResponse;
import com.prgrms.kream.domain.style.model.Feed;
import com.prgrms.kream.domain.style.model.FeedLike;
import com.prgrms.kream.domain.style.model.FeedTag;
import com.prgrms.kream.domain.style.repository.FeedLikeRepository;
import com.prgrms.kream.domain.style.repository.FeedRepository;
import com.prgrms.kream.domain.style.repository.FeedTagRepository;
import com.prgrms.kream.domain.style.service.StyleService;
import com.prgrms.kream.domain.style.service.TagExtractor;

@DisplayName("서비스 계층에서")
@ExtendWith(MockitoExtension.class)
class StyleServiceTest {

	@Mock
	private FeedRepository feedRepository;

	@Mock
	private FeedTagRepository feedTagRepository;

	@Mock
	private FeedLikeRepository feedLikeRepository;

	@InjectMocks
	private StyleService styleService;

	private final Member MEMBER = Member.builder()
			.id(1L)
			.email("kimc980106@naver.com")
			.phone("01086107463")
			.password("qwer1234!")
			.isMale(true)
			.authority(Authority.ROLE_ADMIN)
			.build();

	private final Feed FEED = Feed.builder()
			.id(1L)
			.content("이 피드의 태그는 #총 #두개 입니다.")
			.authorId(MEMBER.getId())
			.build();

	private final Set<FeedTag> FEED_TAGS = TagExtractor.extract(FEED);

	private final FeedLike FEED_LIKE = FeedLike.builder()
			.feedId(FEED.getId())
			.memberId(MEMBER.getId())
			.build();

	@Test
	@DisplayName("피드를 등록할 수 있다.")
	void testRegister() {
		when(feedRepository.save(any())).thenReturn(FEED);
		when(feedTagRepository.saveAll(any())).thenReturn(FEED_TAGS.stream().toList());
		RegisterFeedServiceResponse feedResponse = styleService.register(getRegisterFeedServiceRequest());

		assertThat(feedResponse.id()).isEqualTo(1L);
	}

	@Test
	@DisplayName("피드 컨텐츠로부터 태그를 추출할 수 있다.")
	void testExtractTags() {
		// 주의 : FEED.getContent()의 값에 의존
		assertThat(FEED_TAGS)
				.extracting(FeedTag::getTag)
				.containsExactlyInAnyOrder("#총", "#두개");
	}

	@Test
	@DisplayName("피드를 수정할 수 있다.")
	void testUpdate() {
		when(feedRepository.save(any())).thenReturn(FEED);
		when(feedTagRepository.saveAll(any())).thenReturn(FEED_TAGS.stream().toList());
		styleService.register(getRegisterFeedServiceRequest());
		when(feedRepository.findById(FEED.getId())).thenReturn(Optional.of(FEED));

		UpdateFeedServiceResponse updateFeedServiceResponse =
				styleService.update(
						FEED.getId(),
						getUpdateFeedServiceRequest("이 피드의 태그는 총 #한개 입니다.")
				);

		assertThat(updateFeedServiceResponse.id()).isEqualTo(FEED.getId());
	}

	@Test
	@DisplayName("피드에 사용자의 좋아요를 등록할 수 있다.")
	void testLikeFeed() {
		when(feedLikeRepository.existsByFeedIdAndMemberId(FEED.getId(), MEMBER.getId())).thenReturn(false);
		when(feedLikeRepository.save(any())).thenReturn(FEED_LIKE);

		styleService.registerFeedLike(getLikeFeedServiceRequest());

		verify(feedLikeRepository).existsByFeedIdAndMemberId(FEED.getId(), MEMBER.getId());
		verify(feedLikeRepository).save(any());
	}

	@Test
	@DisplayName("피드에 사용자의 좋아요를 취소할 수 있다.")
	void testUnlikeFeed() {
		doNothing().when(feedLikeRepository).deleteByFeedIdAndMemberId(FEED.getId(), MEMBER.getId());

		styleService.deleteFeedLike(getLikeFeedServiceRequest());

		verify(feedLikeRepository).deleteByFeedIdAndMemberId(FEED.getId(), MEMBER.getId());
	}

	private RegisterFeedServiceRequest getRegisterFeedServiceRequest() {
		return new RegisterFeedServiceRequest(FEED.getContent(), MEMBER.getId());
	}

	private UpdateFeedServiceRequest getUpdateFeedServiceRequest(String content) {
		return new UpdateFeedServiceRequest(content);
	}

	private LikeFeedServiceRequest getLikeFeedServiceRequest() {
		return new LikeFeedServiceRequest(FEED.getId(), MEMBER.getId());
	}

}