package mbr.com.meubattleroyale.MODEL.API;

import android.os.Parcel;
import android.os.Parcelable;

public class Store implements Parcelable {

    private String imageUrl;
    private int manifestId;
    private String name;
    private String rarity;
    private String storeCategory;
    private int vBucks;

    public Store() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getManifestId() {
        return manifestId;
    }

    public void setManifestId(int manifestId) {
        this.manifestId = manifestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getStoreCategory() {
        return storeCategory;
    }

    public void setStoreCategory(String storeCategory) {
        this.storeCategory = storeCategory;
    }

    public int getvBucks() {
        return vBucks;
    }

    public void setvBucks(int vBucks) {
        this.vBucks = vBucks;
    }

    protected Store(Parcel in) {
        imageUrl = in.readString();
        manifestId = in.readInt();
        name = in.readString();
        rarity = in.readString();
        storeCategory = in.readString();
        vBucks = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeInt(manifestId);
        dest.writeString(name);
        dest.writeString(rarity);
        dest.writeString(storeCategory);
        dest.writeInt(vBucks);
    }

    @SuppressWarnings("unused")
    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }

    };


    @Override
    public String toString() {
        return "Store{" +
                "imageUrl='" + imageUrl + '\'' +
                ", manifestId=" + manifestId +
                ", name='" + name + '\'' +
                ", rarity='" + rarity + '\'' +
                ", storeCategory='" + storeCategory + '\'' +
                ", vBucks=" + vBucks +
                '}';
    }
}
