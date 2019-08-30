package in.technitab.teamapp.networking;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;


public interface ApiConfig {

    @Multipart
    @POST("api/UploadImg")
    Call<ServerResponse> UploadImg(
            @PartMap Map<String, RequestBody> map);

    @Multipart
    @POST("upload_image.php")
    Call<ServerResponse> upload(
            @Header("user_id") String userId,
            @Header("base_lat") String latitude,
            @Header("base_long") String base_long,
            @Header("address") String address,
            @Header("type") String type,
            @Header("value") String vender,
            @PartMap Map<String, RequestBody> map);

    @Multipart
    @POST("api/SubmitLocation")
    Call<ServerResponse> submitlocation(
            @Part("purpose") int purpose,
/*            @Part("base_lat") String baselat,
            @Part("base_address") String address,
            @Part("type") String type,*/
            /*@Part("value") String value,*/
            @PartMap Map<String, RequestBody> map
            // @PartMap Map<String, RequestBody> map,
    );
}