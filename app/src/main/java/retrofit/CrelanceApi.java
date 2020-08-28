package retrofit;

import java.util.List;

import data.AdPoster;
import data.Ads;
import data.Benefit;
import data.CategoryListData;
import data.Chats;
import data.Feedback;
import data.Finds;
import data.Messages;
import data.Proposal;
import data.SearchData;
import data.Slides;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CrelanceApi {

    @FormUrlEncoded
    @POST("user/register")
    Call<AdPoster> register(
            @Field("profileImage") String profileImage,
            @Field("location") String location,
            @Field("marketArea") String marketArea,
            @Field("email") String email,
            @Field("businessYear") String businessYear,
            @Field("businessName") String businessName,
            @Field("businessDescription") String businessDescription,
            @Field("serviceDescription") String serviceDescription,
            @Field("phoneNumber") String phoneNumber,
            @Field("verifiedPhoneNumber") String verifiedPhoneNumber,
            @Field("accountNumber") String accountNumber,
            @Field("username") String username,
            @Field("password") String password,
            @Field("userType") String userType
    );

    @FormUrlEncoded
    @POST("user/register")
    Call<AdPoster> registerFind(
            @Field("profileImage") String profileImage,
            @Field("phoneNumber") String phoneNumber,
            @Field("verifiedPhoneNumber") String verifiedPhoneNumber,
            @Field("username") String username,
            @Field("password") String password,
            @Field("userType") String userType
    );

    @GET("user/checkByPhone/{phone}")
    Call<AdPoster> getStatus(@Path("phone") String phone);

    @GET("user/userByAuth/{auth}")
    Call<AdPoster> getUserByAuth(@Path("auth") String auth);

    @GET("user/userById/{id}/{auth}")
    Call<AdPoster> getUserById(@Path("id") String id, @Path("auth") String auth);




    @GET("slide/fetch")
    Call<List<Slides>> getSlides();




    @GET("benefit/fetch")
    Call<List<Benefit>> getBenefits();




    @GET("category/fetchRecent")
    Call<List<CategoryListData>> getCategories();

    @GET("category/fetchAll")
    Call<List<CategoryListData>> getAllCategories();




    @FormUrlEncoded
    @POST("ad/add")
    Call<Ads> addAd(
            @Field("description") String description,
            @Field("title") String title,
            @Field("price") String price,
            @Field("benefit") String benefit,
            @Field("image") String image,
            @Field("category") String category,
            @Field("attachment") String attachment,
            @Field("auth") String auth
    );

    @GET("ad/fetchAll/{count}")
    Call<List<Ads>> getAds(@Path("count") int count);

    @GET("ad/fetchRecent")
    Call<List<Ads>> getRecentAds();

    @GET("ad/fetchByAuth/{auth}/{count}")
    Call<List<Ads>> getAdByAuth(@Path("auth") String auth, @Path("count") int count);

    @GET("ad/fetchByCategory/{categoryId}/{start}")
    Call<List<Ads>> getAdByCategory(@Path("categoryId") String categoryId, @Path("start") int start);

    @GET("ad/fetchDetail/{id}/{auth}")
    Call<Ads> getAdDetail(@Path("id") String id, @Path("auth") String auth);

    @GET("ad/like/{id}/{auth}")
    Call<Ads> likeAd(@Path("id") String id, @Path("auth") String auth);

    @GET("ad/remove/{id}")
    Call<Ads> deleteAd(@Path("id") String id);




    @FormUrlEncoded
    @POST("find/add")
    Call<Finds> addFind(
            @Field("description") String description,
            @Field("title") String title,
            @Field("price") String price,
            @Field("benefit") String benefit,
            @Field("category") String category,
            @Field("attachment") String attachment,
            @Field("chatChoice") String chatChoice,
            @Field("auth") String auth
    );

    @GET("find/fetchAll/{count}")
    Call<List<Finds>> getFinds(@Path("count") int count);

    @GET("find/fetchRecent")
    Call<List<Finds>> getRecentFinds();

    @GET("find/fetchSingleById/{id}")
    Call<Finds> getFind(@Path("id") int id);

    @GET("find/fetchByAuth/{auth}/{start}")
    Call<List<Finds>> getFindByAuth(@Path("auth") String auth, @Path("start") int start);

    @GET("find/fetchByCategory/{categoryId}/{start}")
    Call<List<Finds>> getFindByCategory(@Path("categoryId") String categoryId, @Path("start") int start);

    @GET("find/remove/{id}")
    Call<Finds> deleteFind(@Path("id") String id);




    @FormUrlEncoded
    @POST("proposal/add")
    Call<Proposal> addProposal(
            @Field("description") String description,
            @Field("findId") String findId,
            @Field("benefit") String benefit,
            @Field("chatChoice") String chatChoice,
            @Field("category") String category,
            @Field("auth") String auth
    );

    @GET("proposal/fetch/{findId}/{count}")
    Call<List<Proposal>> getProposal(@Path("findId") String findId, @Path("count") int count);

    @GET("proposal/award/{findId}/{awardedId}")
    Call<Proposal> awardJob(@Path("findId") String findId, @Path("awardedId") String awardedId);

    @GET("proposal/fetchAwarded/{findId}")
    Call<List<Proposal>> fetchAwarded(@Path("findId") String findId);

    @GET("proposal/fetchAwardedCount/{auth}")
    Call<Proposal> fetchAwardedCount(@Path("auth") String auth);




    @FormUrlEncoded
    @POST("message/add")
    Call<Messages> addMessage(
            @Field("message") String message,
            @Field("uniqueId") String uniqueId,
            @Field("findId") String findId,
            @Field("adId") String adId,
            @Field("auth") String auth
    );

    @GET("message/fetch/{auth}/{count}")
    Call<List<Messages>> getMessages(@Path("auth") String auth, @Path("count") int count);

    @GET("message/fetchUnique/{uniqueId}/{auth}")
    Call<List<Messages>> getUniqueMessage(@Path("uniqueId") String uniqueId, @Path("auth") String auth);

    @GET("message/remove/{uniqueId}")
    Call<Messages> removeMessage(@Path("uniqueId") String uniqueId);

    @GET("message/getUnreadCount/{auth}")
    Call<List<Messages>> getUnreadCount(@Path("auth") String auth);

    @GET("message/chatFinish/{uniqueId}")
    Call<Messages> chatFinish(@Path("uniqueId") String uniqueId);




    @GET("search/search/{search}")
    Call<List<SearchData>> getSearch(@Path("search") CharSequence search);





    @FormUrlEncoded
    @POST("feedback/add")
    Call<Feedback> addFeedback(
            @Field("feedback") String feedback,
            @Field("rating") int rating,
            @Field("adId") String adId,
            @Field("type") String type,
            @Field("auth") String auth
    );

    @GET("feedback/fetchByProfile/{userId}/{start}")
    Call<List<Feedback>> getProfileFeedback(@Path("userId") String userId, @Path("start") int start);

    @GET("feedback/fetchByProfileByAuth/{auth}/{start}")
    Call<List<Feedback>> getProfileFeedbackByAuth(@Path("auth") String auth, @Path("start") int start);

    @GET("feedback/fetchByAd/{adId}/{start}")
    Call<List<Feedback>> getAdFeedback(@Path("adId") String adId, @Path("start") int start);





    @FormUrlEncoded
    @POST("report/add")
    Call<Feedback> addReport(
            @Field("report") String feedback,
            @Field("reportedID") String reportedID,
            @Field("auth") String auth
    );
}