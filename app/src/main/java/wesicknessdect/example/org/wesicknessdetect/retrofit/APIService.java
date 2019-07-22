package wesicknessdect.example.org.wesicknessdetect.retrofit;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
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
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.Credential;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticResponse;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;
import wesicknessdect.example.org.wesicknessdetect.models.Model;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.StruggleResponse;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.User;
import wesicknessdect.example.org.wesicknessdetect.models.UserChoice;

public interface APIService {

    @POST("api/reset_password/")
    Call<JsonElement> getNewPassword(@Body JsonObject json);

    @Headers({"Content-Type: application/json","Accept:application/json"})
    @POST("login/")
    Single<User> rxDoLogin(@Body Credential credential);

    @Headers({"Content-Type:application/json","Accept:application/json"})
    @POST("api/users/")
    Single<User> rxDoSignup(@Body User user);

    @POST("api/diagnostic/")
    Single<JsonElement> rxSendDiagnostic(@Header("Authorization") String token, @Body Diagnostic diagnostic);

    @POST("api/picture/")
    Single<JsonElement> rxSendDiagnosticPictures(@Header("Authorization") String token, @Body JsonObject json);

    @Headers({"Content-Type:application/json","Accept:application/json"})
    @POST("api/userchoices/")
    Single<JsonElement> rxSendUserChoices(@Header("Authorization") String token,@Body UserChoice choice);

    @POST("api/pixel/")
    Single<JsonElement> rxSendSymptomRect(@Header("Authorization") String token, @Body JsonObject json);

    @POST("api/post/")
    Single<List<JsonElement>> rxSendMyLocation(@Header("Authorization")String token, @Body RequestBody body);

    @GET("api/questions")
    Single<List<Question>> rxGetQuestion();

    @GET("api/diagnostics/")
    Single <DiagnosticResponse> rxGetDiagnostics(@Header("Authorization") String token,@Query("lastId") int last_id);

    @GET("api/pictures/")
    Single <List<Picture>> rxGetDiagnosticPictures(@Query("diagnostic") long diagnostic,@Header("Authorization") String token);

    @GET("api/pixels")
    Single<List<JsonElement>> rxGetSymptomRect(@Header("Authorization") String token, @Query("pic") int picture_id);

    @GET("api/symptoms")
    Single<List<Symptom>> rxGetSymptoms();

    @GET("api/diseases")
    Single<List<Disease>> rxGetDiseases();

    @GET("api/struggles")
    Single<StruggleResponse> rxGetStruggles();

    @GET("api/country")
    Single<List<Country>> rxGetCountry();

    @GET("api/cultures")
    Single<List<Culture>> rxGetCultures();

    @GET("api/models/")
    Single<List<Model>> rxGetModel(@Query("part") int part_id);

    @GET("api/partcultures/")
    Single<List<CulturePart>> rxGetCulturePart(@Query("culture") int id);

    @PUT("api/user/")
    Single<JsonElement> rxUpdateProfile(@Header("Authorization") String token,@Body JsonObject json);

    @POST("api/size")
    Single<JsonElement> rxGetDataSize(@Header("Authorization") String token);

}
