package com.rookie.code.substream.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
/*

@Serializable
data class RedditPostResponse(
    @SerialName("kind")
    val kind: String?,
    @SerialName("data")
    val data: PostData?
) {
    @Serializable
    data class PostData(
        @SerialName("after")
        val after: String?,
        @SerialName("before")
        val before: String?,
        @SerialName("children")
        val children: List<PostChild?>?,
        @SerialName("dist")
        val dist: Int?,
        @SerialName("modhash")
        val modhash: String?,
        @SerialName("geo_filter")
        val geoFilter: String?
    ) {
        @Serializable
        data class PostChild(
            @SerialName("kind")
            val kind: String?,
            @SerialName("data")
            val data: RedditPost?
        )
    }
}

@Serializable
data class RedditPost(
    @SerialName("id")
    val id: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("author")
    val author: String?,
    @SerialName("subreddit")
    val subreddit: String?,
    @SerialName("subreddit_name_prefixed")
    val subredditNamePrefixed: String?,
    @SerialName("subreddit_subscribers")
    val subredditSubscribers: Int?,
    @SerialName("url")
    val url: String?,
    @SerialName("url_overridden_by_dest")
    val urlOverriddenByDest: String? = null,
    @SerialName("domain")
    val domain: String?,
    @SerialName("permalink")
    val permalink: String?,
    @SerialName("created_utc")
    val createdUtc: Double?,
    @SerialName("created")
    val created: Double?,
    @SerialName("score")
    val score: Int?,
    @SerialName("ups")
    val ups: Int?,
    @SerialName("downs")
    val downs: Int?,
    @SerialName("upvote_ratio")
    val upvoteRatio: Double?,
    @SerialName("num_comments")
    val numComments: Int?,
    @SerialName("is_video")
    val isVideo: Boolean?,
    @SerialName("is_self")
    val isSelf: Boolean?,
    @SerialName("is_meta")
    val isMeta: Boolean?,
    @SerialName("is_original_content")
    val isOriginalContent: Boolean?,
    @SerialName("is_reddit_media_domain")
    val isRedditMediaDomain: Boolean?,
    @SerialName("selftext")
    val selftext: String?,
    @SerialName("selftext_html")
    val selftextHtml: String?,
    @SerialName("thumbnail")
    val thumbnail: String?,
    @SerialName("thumbnail_height")
    val thumbnailHeight: Int? = null,
    @SerialName("thumbnail_width")
    val thumbnailWidth: Int? = null,
    @SerialName("over_18")
    val over18: Boolean?,
    @SerialName("spoiler")
    val spoiler: Boolean?,
    @SerialName("locked")
    val locked: Boolean?,
    @SerialName("archived")
    val archived: Boolean?,
    @SerialName("clicked")
    val clicked: Boolean?,
    @SerialName("saved")
    val saved: Boolean?,
    @SerialName("stickied")
    val stickied: Boolean?,
    @SerialName("pinned")
    val pinned: Boolean?,
    @SerialName("contest_mode")
    val contestMode: Boolean?,
    @SerialName("no_follow")
    val noFollow: Boolean?,
    @SerialName("is_crosspostable")
    val isCrosspostable: Boolean?,
    @SerialName("media_only")
    val mediaOnly: Boolean?,
    @SerialName("can_gild")
    val canGild: Boolean?,
    @SerialName("can_mod_post")
    val canModPost: Boolean?,
    @SerialName("author_premium")
    val authorPremium: Boolean?,
    @SerialName("author_patreon_flair")
    val authorPatreonFlair: Boolean?,
    @SerialName("author_flair_type")
    val authorFlairType: String?,
    @SerialName("author_flair_text")
    val authorFlairText: String?,
    @SerialName("author_flair_text_color")
    val authorFlairTextColor: String?,
    @SerialName("author_flair_background_color")
    val authorFlairBackgroundColor: String?,
    @SerialName("author_flair_css_class")
    val authorFlairCssClass: String?,
    @SerialName("author_flair_template_id")
    val authorFlairTemplateId: String?,
    @SerialName("author_flair_richtext")
    val authorFlairRichtext: List<FlairRichtext?>?,
    @SerialName("link_flair_type")
    val linkFlairType: String?,
    @SerialName("link_flair_text")
    val linkFlairText: String?,
    @SerialName("link_flair_text_color")
    val linkFlairTextColor: String?,
    @SerialName("link_flair_background_color")
    val linkFlairBackgroundColor: String?,
    @SerialName("link_flair_css_class")
    val linkFlairCssClass: String?,
    @SerialName("link_flair_richtext")
    val linkFlairRichtext: List<FlairRichtext?>?,
    @SerialName("preview")
    val preview: PostPreview? = null,
    @SerialName("media")
    val media: PostMedia?,
    @SerialName("secure_media")
    val secureMedia: PostMedia?,
    @SerialName("media_embed")
    val mediaEmbed: MediaEmbed?,
    @SerialName("secure_media_embed")
    val secureMediaEmbed: MediaEmbed?,
    @SerialName("gildings")
    val gildings: Gildings?,
    @SerialName("all_awardings")
    val allAwardings: List<Awarding?>?,
    @SerialName("awarders")
    val awarders: List<String?>?,
    @SerialName("treatment_tags")
    val treatmentTags: List<String?>?,
    @SerialName("is_robot_indexable")
    val isRobotIndexable: Boolean?,
    @SerialName("is_created_from_ads_ui")
    val isCreatedFromAdsUi: Boolean?,
    @SerialName("send_replies")
    val sendReplies: Boolean?,
    @SerialName("visited")
    val visited: Boolean?,
    @SerialName("likes")
    val likes: Boolean?,
    @SerialName("view_count")
    val viewCount: Int?,
    @SerialName("edited")
    val edited: Boolean?,
    @SerialName("name")
    val name: String?,
    @SerialName("subreddit_id")
    val subredditId: String?,
    @SerialName("author_fullname")
    val authorFullname: String?,
    @SerialName("author_is_blocked")
    val authorIsBlocked: Boolean?,
    @SerialName("mod_reports")
    val modReports: List<List<String?>?>?,
    @SerialName("user_reports")
    val userReports: List<List<String?>?>?,
    @SerialName("report_reasons")
    val reportReasons: String?,
    @SerialName("removed_by_category")
    val removedByCategory: String?,
    @SerialName("content_categories")
    val contentCategories: String?,
    @SerialName("discussion_type")
    val discussionType: String?,
    @SerialName("category")
    val category: String?,
    @SerialName("top_awarded_type")
    val topAwardedType: String?,
    @SerialName("banned_by")
    val bannedBy: String?,
    @SerialName("removed_by")
    val removedBy: String?,
    @SerialName("num_reports")
    val numReports: Int?,
    @SerialName("approved_by")
    val approvedBy: String?,
    @SerialName("approved_at_utc")
    val approvedAtUtc: Double?,
    @SerialName("banned_at_utc")
    val bannedAtUtc: Double?,
    @SerialName("mod_reason_by")
    val modReasonBy: String?,
    @SerialName("mod_reason_title")
    val modReasonTitle: String?,
    @SerialName("removal_reason")
    val removalReason: String?,
    @SerialName("quarantine")
    val quarantine: Boolean?,
    @SerialName("pwls")
    val pwls: Int?,
    @SerialName("wls")
    val wls: Int?,
    @SerialName("hide_score")
    val hideScore: Boolean?,
    @SerialName("suggested_sort")
    val suggestedSort: String?,
    @SerialName("gilded")
    val gilded: Int?,
    @SerialName("total_awards_received")
    val totalAwardsReceived: Int?,
    @SerialName("distinguished")
    val distinguished: String?,
    @SerialName("post_hint")
    val postHint: String? = null,
    @SerialName("num_crossposts")
    val numCrossposts: Int?
) {
    @Serializable
    data class FlairRichtext(
        @SerialName("e")
        val e: String?,
        @SerialName("t")
        val t: String?
    )

    @Serializable
    data class PostPreview(
        @SerialName("enabled")
        val enabled: Boolean?,
        @SerialName("images")
        val images: List<PreviewImage?>?
    ) {
        @Serializable
        data class PreviewImage(
            @SerialName("source")
            val source: ImageSource?,
            @SerialName("resolutions")
            val resolutions: List<ImageResolution?>?,
            @SerialName("variants")
            val variants: ImageVariants?,
            @SerialName("id")
            val id: String?
        ) {
            @Serializable
            data class ImageSource(
                @SerialName("url")
                val url: String?,
                @SerialName("width")
                val width: Int?,
                @SerialName("height")
                val height: Int?
            )

            @Serializable
            data class ImageResolution(
                @SerialName("url")
                val url: String?,
                @SerialName("width")
                val width: Int?,
                @SerialName("height")
                val height: Int?
            )

            @Serializable
            data class ImageVariants(
                @SerialName("placeholder")
                val placeholder: String? = null
            )
        }
    }

    @Serializable
    data class PostMedia(
        @SerialName("type")
        val type: String?,
        @SerialName("oembed")
        val oembed: MediaOembed?
    ) {
        @Serializable
        data class MediaOembed(
            @SerialName("provider_url")
            val providerUrl: String?,
            @SerialName("title")
            val title: String?,
            @SerialName("html")
            val html: String?,
            @SerialName("thumbnail_width")
            val thumbnailWidth: Int?,
            @SerialName("height")
            val height: Int?,
            @SerialName("width")
            val width: Int?,
            @SerialName("version")
            val version: String?,
            @SerialName("author_name")
            val authorName: String?,
            @SerialName("provider_name")
            val providerName: String?,
            @SerialName("thumbnail_url")
            val thumbnailUrl: String?,
            @SerialName("type")
            val type: String?,
            @SerialName("thumbnail_height")
            val thumbnailHeight: Int?,
            @SerialName("author_url")
            val authorUrl: String?
        )
    }

    @Serializable
    data class MediaEmbed(
        @SerialName("content")
        val content: String? = null
    )

    @Serializable
    data class Gildings(
        @SerialName("gid_1")
        val gid1: Int? = null
    )

    @Serializable
    data class Awarding(
        @SerialName("giver_coin_reward")
        val giverCoinReward: Int?,
        @SerialName("subreddit_id")
        val subredditId: String?,
        @SerialName("is_new")
        val isNew: Boolean?,
        @SerialName("days_of_drip_extension")
        val daysOfDripExtension: Int?,
        @SerialName("coin_price")
        val coinPrice: Int?,
        @SerialName("id")
        val id: String?,
        @SerialName("penny_donate")
        val pennyDonate: Int?,
        @SerialName("award_sub_type")
        val awardSubType: String?,
        @SerialName("coin_reward")
        val coinReward: Int?,
        @SerialName("icon_url")
        val iconUrl: String?,
        @SerialName("days_of_premium")
        val daysOfPremium: Int?,
        @SerialName("tiers_by_coin")
        val tiersByCoin: Map<String, Int>?,
        @SerialName("resized_icons")
        val resizedIcons: List<ResizedIcon?>?,
        @SerialName("icon_width")
        val iconWidth: Int?,
        @SerialName("static_icon_width")
        val staticIconWidth: Int?,
        @SerialName("start_date")
        val startDate: Double?,
        @SerialName("is_enabled")
        val isEnabled: Boolean?,
        @SerialName("awardings_required_to_grant_benefits")
        val awardingsRequiredToGrantBenefits: Int?,
        @SerialName("description")
        val description: String?,
        @SerialName("end_date")
        val endDate: Double?,
        @SerialName("subreddit_coin_reward")
        val subredditCoinReward: Int?,
        @SerialName("count")
        val count: Int?,
        @SerialName("static_icon_height")
        val staticIconHeight: Int?,
        @SerialName("name")
        val name: String?,
        @SerialName("resized_static_icons")
        val resizedStaticIcons: List<ResizedIcon?>?,
        @SerialName("icon_format")
        val iconFormat: String?,
        @SerialName("icon_height")
        val iconHeight: Int?,
        @SerialName("penny_price")
        val pennyPrice: Int?,
        @SerialName("award_type")
        val awardType: String?,
        @SerialName("static_icon_url")
        val staticIconUrl: String?
    ) {
        @Serializable
        data class ResizedIcon(
            @SerialName("url")
            val url: String?,
            @SerialName("width")
            val width: Int?,
            @SerialName("height")
            val height: Int?
        )
    }
}
*/
