package by.story_weaver.worldradiomonitoring.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationUtil {
    public static List<String> getCountryNamesFromIso(List<String> isoCodes) {
        Locale userLocale = Locale.getDefault();
        List<String> result = new ArrayList<>();

        for (String iso : isoCodes) {
            if (iso == null || iso.trim().isEmpty()) continue;

            Locale locale = new Locale("", iso);
            String name = locale.getDisplayCountry(userLocale);

            if (!name.isEmpty()) {
                result.add(name);
            } else {
                result.add(iso);
            }
        }

        return result;
    }

    public static String getCountryNameFromIso(String isoCode) {
        if (isoCode == null || isoCode.trim().isEmpty()) {
            return null;
        }

        Locale userLocale = Locale.getDefault();
        Locale locale = new Locale("", isoCode);

        String countryName = locale.getDisplayCountry(userLocale);

        if (!countryName.isEmpty()) {
            return countryName;
        }

        return isoCode;
    }

    public static String getIsoFromCountryName(String countryName) {
        if (countryName == null || countryName.trim().isEmpty()) return null;

        Locale userLocale = Locale.getDefault();
        String[] isoCountries = Locale.getISOCountries();

        for (String iso : isoCountries) {
            Locale locale = new Locale("", iso);
            String localizedName = locale.getDisplayCountry(userLocale);

            if (localizedName.equalsIgnoreCase(countryName.trim())) {
                return iso;
            }
        }

        return null;
    }


}

