package org.naturenet.data.model;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SiteParcelizationTests {


    static String ID = "df8sd3AfEQFr4";
    static String NAME = "Reedy Creek";
    static String DESCRIPTION = "Park";
    static String GEOHASH = "7zzzzzzzzz";

    @Test
    public void test_is_site_parcelable(){

        final ArrayList<Double> locList = new ArrayList<>();

        locList.add(23.5);
        locList.add(46.5);
        locList.add(95.23);
        locList.add(10.4);
        locList.add(26.3);
        locList.add(93.5);

        Site site = new Site(ID, NAME, DESCRIPTION, locList, GEOHASH);

        Parcel parcel = Parcel.obtain();

        site.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        Site parceledSite = Site.CREATOR.createFromParcel(parcel);

        assertEquals(null, parceledSite.id, ID);
        assertEquals(null, parceledSite.name, NAME);
        assertEquals(null, parceledSite.description, DESCRIPTION);
        assertEquals(null, parceledSite.location, locList);
    }
}
