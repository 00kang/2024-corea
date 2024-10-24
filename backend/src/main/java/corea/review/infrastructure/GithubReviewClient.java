package corea.review.infrastructure;

import corea.auth.infrastructure.GithubPersonalAccessTokenProvider;
import corea.review.dto.GithubPullRequestReview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@RequiredArgsConstructor
public class GithubReviewClient {

    private final RestClient restClient;
    private final GithubPullRequestUrlExchanger githubPullRequestUrlExchanger;
    private final GithubPersonalAccessTokenProvider githubPersonalAccessTokenProvider;

    public List<GithubPullRequestReview> getPullRequestReviews(String prLink) {
        String reviewApiUrl = githubPullRequestUrlExchanger.prLinkToReviewApiUrl(prLink);

        return Stream.iterate(1, page -> page + 1)
                .map(page -> getPullRequestReviewsForPage(page, reviewApiUrl))
                .takeWhile(this::hasMoreReviews)
                .flatMap(Arrays::stream)
                .toList();
    }

    private GithubPullRequestReview[] getPullRequestReviewsForPage(int page, String reviewApiUrl) {
        String url = buildPageUrl(page, reviewApiUrl);

        return restClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, githubPersonalAccessTokenProvider.getRandomPersonalAccessToken())
                .accept(APPLICATION_JSON)
                .retrieve()
                .body(GithubPullRequestReview[].class);
    }

    private String buildPageUrl(int page, String reviewApiUrl) {
        return reviewApiUrl + "?page=" + page + "&per_page=100";
    }

    private boolean hasMoreReviews(GithubPullRequestReview[] reviews) {
        return reviews.length > 0;
    }
}
