package wesicknessdect.example.org.wescanleaf.retrofit;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;
import wesicknessdect.example.org.wescanleaf.models.Country;
import wesicknessdect.example.org.wescanleaf.models.Credential;
import wesicknessdect.example.org.wescanleaf.models.Culture;
import wesicknessdect.example.org.wescanleaf.models.CulturePart;
import wesicknessdect.example.org.wescanleaf.models.Diagnostic;
import wesicknessdect.example.org.wescanleaf.models.DiagnosticResponse;
import wesicknessdect.example.org.wescanleaf.models.Disease;
import wesicknessdect.example.org.wescanleaf.models.Model;
import wesicknessdect.example.org.wescanleaf.models.Picture;
import wesicknessdect.example.org.wescanleaf.models.Question;
import wesicknessdect.example.org.wescanleaf.models.StruggleResponse;
import wesicknessdect.example.org.wescanleaf.models.Symptom;
import wesicknessdect.example.org.wescanleaf.models.User;
import wesicknessdect.example.org.wescanleaf.models.UserChoice;

public interface APIService {
    @GET
    Call<ResponseBody> downloadModelWithDynamicUrlSync(@Url String fileUrl);

    @Headers({"Content-Type: application/json","Accept:application/json"})
    @POST("login/")
    Call<User> doLogin(@Body Credential credential);

    @Headers({"Content-Type:application/json","Accept:application/json"})
    @POST("api/users/")
    Call<User> doSignup(@Body User user);

    //@Multipart

    //@Headers({"Content-Type:application/json","Accept:application/json"})
//    @FormUrlEncoded
    //@Multipart
    @POST("api/post/")
    Call<List<JsonElement>> sendMyLocation(@Header("Authorization")String token, @Body RequestBody body);



    @GET("api/country/")
    Call<List<Country>> getCountries();

    @GET("api/cultures/")
    Call<List<Culture>> getCultures();

    @GET("api/partcultures/")
    Call<List<CulturePart>> getCulturePart(@Query("culture") int id);

    @GET("api/models/")
    Call<List<Model>> getModel(@Query("part") int part_id);

    @Headers({"Content-Type:application/json","Accept:application/json"})
    @POST("api/userchoices/")
    Call<JsonElement> sendUserChoices(@Header("Authorization") String token,@Body UserChoice choice);

    @POST("api/diagnostic/")
    Call<JsonElement> sendDiagnostic(@Header("Authorization") String token, @Body Diagnostic diagnostic);

    @POST("api/pixel/")
    Call<JsonElement> sendSymptomRect(@Header("Authorization") String token, @Body JsonObject json);


    @GET("api/pixels")
    Call<List<JsonElement>> getSymptomRect(@Header("Authorization") String token, @Query("pic") int picture_id);

    @POST("api/picture/")
    Call<JsonElement> sendDiagnosticPictures(@Header("Authorization") String token, @Body JsonObject json);

    @GET("api/questions")
    Call<List<Question>> getQuestion();

    @GET("api/symptoms")
    Call<List<Symptom>> getSymptoms();

    @GET("api/diseases")
    Call<List<Disease>> getDiseases();

    @GET("api/struggles")
    Call<StruggleResponse> getStruggles();

    @GET("api/diagnostics/")
    Call <DiagnosticResponse> getDiagnostics(@Header("Authorization") String token, @Query("lastId") int last_id);

    @GET("api/pictures/")
    Call <List<Picture>> getDiagnosticPictures(@Query("diagnostic") long diagnostic, @Header("Authorization") String token);

    @PUT("api/user/")
    Call<JsonElement> updateProfile(@Header("Authorization") String token,@Body JsonObject json);

    @POST("api/reset_password/")
    Call<JsonElement> getNewPassword(@Body JsonObject json);

}
