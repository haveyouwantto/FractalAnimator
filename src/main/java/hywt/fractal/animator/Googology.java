package hywt.fractal.animator;

public class Googology {
    private static final String[] basics = {"n", "m", "b", "tr", "quadr", "quint", "sext", "sept", "oct", "non"};
    private static final String[] units = {"", "un", "duo", "tre", "quattuor", "quin", "se", "septe", "octo", "nove"};
    private static final String[] tens = {"", "deci", "viginti", "triginta", "quadraginta", "quinquaginta", "sexaginta", "septuaginta",
            "octoginta", "nonaginta"};
    private static final String[] tensMarker = {"", "n", "ms", "ns", "ns", "ns", "n", "n", "mx", ""};
    private static final String[] hundreds = {"", "centi", "ducenti", "trecenti", "quadringenti", "quingenti", "sescenti", "septingenti",
            "octingenti", "nongenti"};
    private static final String[] hundredsMarker = {"", "nx", "n", "ns", "ns", "ns", "n", "n", "mx", ""};
    private static final String vowels = "aeiou";
    private static final String[] suffixes = {
            "", "thousand"
    };

    // Helper function to remove last vowel
    public static String removeLastVowel(String s) {
        if (!s.isEmpty() && vowels.indexOf(s.charAt(s.length() - 1)) >= 0) {
            return s.substring(0, s.length() - 1); // Remove the last character if it's a vowel
        }
        return s; // Return the original string if the last character is not a vowel
    }

    // Function to generate googology suffix for numbers
    public static String googologySuffix(int n) {

        String result;
        if (n % 1000 < 10) {
            result = basics[n % 1000];
        } else {
            int unitOrd = n % 10;
            String unit = units[unitOrd];
            String ten = tens[(n / 10) % 10];
            String hundred = hundreds[(n / 100) % 10];

            String marker = ten.isEmpty() ? hundredsMarker[(n / 100) % 10] : tensMarker[(n / 10) % 10];

            switch (unitOrd) {
                case 3:
                    if (marker.contains("s")) unit += "s";
                    break;
                case 6:
                    if (marker.contains("s")) unit += "s";
                    if (marker.contains("x")) unit += "x";
                    break;
                case 7:
                case 9:
                    if (marker.contains("n")) unit += "n";
                    if (marker.contains("m")) unit += "m";
                    break;
                default:
                    break;
            }

            result = unit + ten + hundred;
        }

        if (n >= 1000) {
            result = googologySuffix(n / 1000) + result;
        }

        return removeLastVowel(result) + "illi";
    }

    // Function to get suffix based on order
    public static String getSuffix(int ord) {
        if (ord < 0) return "";
        if (ord < suffixes.length) return suffixes[ord];
        return googologySuffix(ord - 1) + "on";
    }

    // Convert number to English notation based on mantissa and exponent
    public static String toEnglishNotation(double mant, int exp) {
        double significantDigits = mant * Math.pow(10, exp % 3);
        int thousandsExponent = exp / 3;
        String suffix = getSuffix(thousandsExponent);
        String formattedNumber = String.format("%.3f", significantDigits);
        return formattedNumber + " " + suffix;
    }

    // Function to format the logarithm base 2
    public static String googologyFormatLog2(double log2) {
        LogResult result = fromLog2(log2);
        return toEnglishNotation(result.mant, result.exp);
    }

    // Function to format the logarithm base 10
    public static String googologyFormatLog10(double log10) {
        LogResult result = fromLog10(log10);
        return toEnglishNotation(result.mant, result.exp);
    }

    // Function to format scientific notation
    public static String googologyFormatScientific(String scientific) {
        LogResult result = fromScientific(scientific);
        return toEnglishNotation(result.mant, result.exp);
    }

    // Helper functions for logarithmic conversions
    public static LogResult fromLog2(double val) {
        double log10 = val * Math.log10(2); // Convert log2 to log10
        double fractionalPart = log10 - Math.floor(log10); // Extract fractional part
        int integerPart = (int) Math.floor(log10); // Extract integer part
        return new LogResult(Math.pow(10, fractionalPart), integerPart);
    }

    public static LogResult fromLog10(double val) {
        double fractionalPart = val - Math.floor(val); // Extract fractional part
        int integerPart = (int) Math.floor(val); // Extract integer part
        return new LogResult(Math.pow(10, fractionalPart), integerPart);
    }

    public static LogResult fromScientific(String val) {
        // Split the input string into mantissa and exponent parts
        String[] parts = val.toLowerCase().split("e");
        double mant = Double.parseDouble(parts[0]);
        int exp = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

        // Normalize mantissa to be between 1 and 10
        if (mant != 0 && (mant < 1 || mant >= 10)) {
            int scale = (int) Math.floor(Math.log10(Math.abs(mant)));
            mant = mant / Math.pow(10, scale);
            exp += scale;
        }

        return new LogResult(mant, exp);
    }

    // LogResult class to hold mantissa and exponent
    public static class LogResult {
        double mant;
        int exp;

        public LogResult(double mant, int exp) {
            this.mant = mant;
            this.exp = exp;
        }
    }
}

