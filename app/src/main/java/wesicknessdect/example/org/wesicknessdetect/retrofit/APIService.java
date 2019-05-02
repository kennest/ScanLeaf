package wesicknessdect.example.org.wesicknessdetect.retrofit;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
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
import wesicknessdect.example.org.wesicknessdetect.models.Struggle;
import wesicknessdect.example.org.wesicknessdetect.models.StruggleResponse;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.models.User;

public interface APIService {
    @GET
    Call<ResponseBody> downloadModelWithDynamicUrlSync(@Url String fileUrl);

    @Headers({"Content-Type: application/json","Accept:application/json"})
    @POST("login/")
    Call<User> doLogin(@Body Credential credential);

    @Headers({"Content-Type:application/json","Accept:application/json"})
    @POST("api/users/")
    Call<User> doSignup(@Body User user);

    @GET("api/country/")
    Call<List<Country>> getCountries();

    @GET("api/cultures/")
    Call<List<Culture>> getCultures();

    @GET("api/partcultures/")
    Call<List<CulturePart>> getCulturePart(@Query("culture") int id);

    @GET("api/models/")
    Call<List<Model>> getModel(@Query("part") int part_id);

    @GET("api/models")
    Call<Model> getModels(@Header("Authorization") String token);

    @POST("api/diagnostic/")
    Call<JsonElement> sendDiagnostic(@Header("Authorization") String token, @Body Diagnostic diagnostic);

    @POST("api/pixel/")
    Call<JsonElement> sendSymptomRect(@Header("Authorization") String token, @Body JsonObject json);


    @GET("api/pixels")
    Call<List<JsonElement>> getSymptomRect(@Header("Authorization") String token, @Query("picture") int picture_id);

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
    Call <DiagnosticResponse> getDiagnostics(@Header("Authorization") String token);

    @GET("api/pictures/")
    Call <List<Picture>> getDiagnosticPictures(@Query("diagnostic") long diagnostic,@Header("Authorization") String token);

}
