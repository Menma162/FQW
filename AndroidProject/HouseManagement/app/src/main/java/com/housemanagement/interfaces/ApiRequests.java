package com.housemanagement.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.housemanagement.models.other.AdvertisementPostToServer;
import com.housemanagement.models.other.AdvertisementPutToServer;
import com.housemanagement.models.other.ComplaintToServer;
import com.housemanagement.models.other.IndicationToServer;
import com.housemanagement.models.other.UserLogin;
import com.housemanagement.models.other.UserToServer;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiRequests {
    @POST("api/authenticate/login")
    Call<JsonObject> login(@Body UserLogin user);
    @GET("/api/user/username/{username}")
    Call<JsonObject> getUser(@Header("Authorization") String token, @Path("username") String username);
    @GET("/api/flatowner/{id}")
    Call<JsonObject> getFlatOwnerById(@Header("Authorization") String token, @Path("id") Integer id);
    @GET("/api/flatowner/admin/{id}")
    Call<JsonArray> getFlatOwners(@Header("Authorization") String token, @Path("id") String id);
    @GET("/api/flat/flatowner/{id}")
    Call<JsonArray> getFlatsByIdFlatOwner(@Header("Authorization") String token, @Path("id") Integer id);
    @GET("/api/flat/admin/{id}")
    Call<JsonArray> getFlats(@Header("Authorization") String token, @Path("id") String id);
    @GET("/api/counter/flatowner/{id}")
    Call<JsonArray> getCountersByIdFlatOwnerAndUsed(@Header("Authorization") String token, @Path("id") Integer id);
    @GET("/api/indication/flatowner/{id}")
    Call<JsonArray> getIndicationsByIdFlatOwner(@Header("Authorization") String token, @Path("id") Integer id);
    @GET("/api/payment/flatowner/{id}")
    Call<JsonArray> getPaymentsByIdFlatOwner(@Header("Authorization") String token, @Path("id") Integer id);
    @GET("/api/advertisement/admin/{id}")
    Call<JsonArray> getAdvertisements(@Header("Authorization") String token, @Path("id") String id);
    @GET("/api/advertisement/flatowner/{id}")
    Call<JsonArray> getAdvertisements(@Header("Authorization") String token, @Path("id") Integer id);
    @GET("/api/complaint/flatowner/{id}")
    Call<JsonArray> getComplaintsByIdFlatOwner(@Header("Authorization") String token, @Path("id") Integer id);
    @GET("/api/complaint/admin/{id}")
    Call<JsonArray> getComplaints(@Header("Authorization") String token, @Path("id") String id);
    @GET("/api/house/byuser/{id}")
    Call<JsonArray> getHouses(@Header("Authorization") String token, @Path("id") String id);
    @GET("/api/service")
    Call<JsonArray> getServices(@Header("Authorization") String token);
    @GET("/api/settingsservice/flatowner/{id}")
    Call<JsonArray> getSettingsServices(@Header("Authorization") String token, @Path("id") Integer id);
    @GET("/api/user/dateNow")
    Call<JsonPrimitive> getDateNow(@Header("Authorization") String token);
    @GET("/api/complaint/getPhotoMobile/{id}")
    Call<JsonPrimitive> getPhoto(@Header("Authorization") String token, @Path("id") Integer id);
    @GET("/api/complaint/getPhotoMobile/admin/{id}")
    Call<JsonArray> getPhotoByAdmin(@Header("Authorization") String token, @Path("id") String id);
    @GET("/api/complaint/getPhotoMobile/flatowner/{id}")
    Call<JsonArray> getPhotoByFlatOwner(@Header("Authorization") String token, @Path("id") Integer id);
    @POST("/api/complaint")
    Call<JsonObject> sendComplaint(@Header("Authorization") String token, @Body ComplaintToServer complaint);
    @PUT("/api/complaint/{id}")
    Call<JsonObject> putComplaint(@Header("Authorization") String token, @Path("id") Integer id, @Body ComplaintToServer complaint);
    @POST("/api/advertisement")
    Call<JsonArray> sendAdvertisement(@Header("Authorization") String token, @Body AdvertisementPostToServer advertisement);
    @PUT("/api/advertisement/{id}")
    Call<JsonObject> putAdvertisement(@Header("Authorization") String token, @Path("id") Integer id, @Body AdvertisementPutToServer advertisement);
    @DELETE("/api/advertisement/{id}")
    Call<JsonObject> deleteAdvertisement(@Header("Authorization") String token, @Path("id") Integer id);
    @DELETE("/api/complaint/{id}")
    Call<JsonObject> deleteComplaint(@Header("Authorization") String token, @Path("id") Integer id);
    @DELETE("/api/complaint/photo/{id}/{name}")
    Call<JsonObject> deletePhotoComplaint(@Header("Authorization") String token, @Path("id") Integer id, @Path("name") String name);
    @POST("/api/indication/fromOwner")
    Call<JsonObject> sendIndication(@Header("Authorization") String token, @Body IndicationToServer indication);
    @PUT("/api/indication/fromOwner/{id}")
    Call<JsonObject> putIndication(@Header("Authorization") String token, @Path("id") Integer id, @Body IndicationToServer indication);
    @PUT("/api/user/login/{id}")
    Call<JsonObject> putUserLogin(@Header("Authorization") String token, @Path("id") String id, @Body UserToServer user);
    @Multipart
    @PUT("/api/complaint/photo/{id}")
    Call<JsonObject> putPhoto(@Header("Authorization") String token, @Path("id") Integer id, @Part MultipartBody.Part photo);
    @PUT("/api/user/password/{id}")
    Call<JsonObject> putUserPassword(@Header("Authorization") String token, @Path("id") String id, @Body UserToServer user);
    @Multipart
    @POST("api/complaint/uploadFile/{idHouse}/{idFlat}")
    Call<ResponseBody> uploadPicture(@Header("Authorization") String token, @Part MultipartBody.Part part, @Path("idHouse") Integer idHouse,@Path("idFlat") Integer idFlat);
}
