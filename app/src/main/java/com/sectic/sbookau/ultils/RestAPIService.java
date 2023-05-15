package com.sectic.sbookau.ultils;

import com.sectic.sbookau.model.AudioPart;
import com.sectic.sbookau.model.BaseResponse;
import com.sectic.sbookau.model.Book;
import com.sectic.sbookau.model.BookList;
import com.sectic.sbookau.model.AudioPartList;
import com.sectic.sbookau.model.CatalogList;
import com.sectic.sbookau.model.Message;
import com.sectic.sbookau.model.User;
import com.sectic.sbookau.model.UserBook;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by bioz on 6/22/2017.
 */

public interface RestAPIService {
    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlAsync(@Url String fileUrl);

    @POST("login")
    Call<User> login(@Body User user);
    @POST("login_google")
    Call<User> loginGoogle(@Body User user);

    @GET("auth/tags")
    Call<CatalogList> getTags(@Header("x-access-token") String token, @Query("filter") String sFilter);

    @GET("auth/books")
    Call<BookList> getBooks(@Header("x-access-token") String token, @Query("filter") String sFilter, @Query("q") String q,
                                                                    @Query("page") int page, @Query("perPage") int perPage);
    @PUT("auth/books/{id}")
    Call<Book> updateBooks(@Header("x-access-token") String token, @Path("id") String sId, @Body Book book);

    @GET("auth/book_parts")
    Call<AudioPartList> getBookParts(@Header("x-access-token") String token, @Query("filter") String sFilter, @Query("sort") String sSort,
                                     @Query("page") int page, @Query("perPage") int perPage);
    @PUT("auth/book_parts/{id}")
    Call<AudioPart> updateAudioParts(@Header("x-access-token") String token, @Path("id") String sId, @Body AudioPart audio);

    @POST("auth/messages/send_system")
    Call<BaseResponse> sendtosystem(@Header("x-access-token") String token, @Body Message message);

    @POST("auth/user_books")
    Call<UserBook> modifyUserBook(@Header("x-access-token") String token, @Body UserBook oUserBook);
    @GET("auth/user_books/creator_book")
    Call<UserBook> getByCreatorAndBook(@Header("x-access-token") String token, @Query("creator") String sCreator, @Query("book") String sBook);
}
