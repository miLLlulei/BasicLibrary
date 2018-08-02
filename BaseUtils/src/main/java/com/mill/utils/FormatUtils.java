package com.mill.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Formatter;
import android.util.Pair;

public class FormatUtils {
    public static String retriveDigit(String paramString) {
        StringBuilder localStringBuilder = new StringBuilder();
        int len = paramString.length();
        for (int i = 0; i < len; ++i) {
            char c = paramString.charAt(i);
            if (c < '0' || c > '9') {
                continue;
            }
            localStringBuilder.append(c);
        }

        if (localStringBuilder.length() <= 0) {
            return "0";
        } else {
            return localStringBuilder.toString();
        }
    }

    @SuppressLint("DefaultLocale")
    public static String formatSize(long number) {
        if (number <= 1024) {
            return number + "B";
        } else if (number <= 10 * 1024) {
            return number / 1024 + "KB";
        }

        float result = number * 1.0f / (1024.0f * 1024.0f);
        String suffix = "M";

        if (result > 900) {
            suffix = "G";
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 10) {
            value = String.format("%.2f", result);
        } else if (result < 100) {
            value = String.format("%.2f", result);
        } else {
            value = String.format("%.2f", result);
        }
        return value + suffix;
    }

    public static String formatSizeEx(long number) {
        if (number <= 1024) {
            return number + "B";
        } else if (number <= 10 * 1024) {
            return number / 1024 + "KB";
        }

        float result = number * 1.0f / (1024.0f * 1024.0f);
        String suffix = "M";

        if (result > 900) {
            suffix = "G";
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.1f", result);
        } else if (result < 10) {
            value = String.format("%.1f", result);
        } else if (result < 100) {
            value = String.format("%.1f", result);
        } else {
            value = String.format("%.1f", result);
        }
        return value + suffix;
    }

    public static String formatSizeMax3(long number) {
        if (number <= 1024) {
            return number + "B";
        } else if (number <= 10 * 1024) {
            return number / 1024 + "KB";
        }

        float result = number * 1.0f / (1024.0f * 1024.0f);
        String suffix = "M";

        if (result > 900) {
            suffix = "G";
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 10) {
            value = String.format("%.2f", result);
        } else if (result < 100) {
            value = String.format("%.1f", result);
        } else {
            value = String.format("%.0f", result);
        }
        return value + suffix;
    }

    public static String formatTime(int second) {
        if (second > 24 * 3600) {
            return "";
        }
        if (second < 60) {
            if (second < 10) {
                return "00:0" + String.valueOf(second);
            } else {
                return "00:" + String.valueOf(second);
            }
        } else if (second < 60 * 60) {
            int minute = second / 60;
            int second2 = second % 60;

            if (minute < 10) {
                if (second2 < 10) {
                    return "0" + minute + ":0" + String.valueOf(second2);
                } else {
                    return "0" + minute + ":" + String.valueOf(second2);
                }
            } else {
                if (second2 < 10) {
                    return minute + ":0" + String.valueOf(second2);
                } else {
                    return minute + ":" + String.valueOf(second2);
                }
            }
        } else {
            int hour = second / 3600;
            int minute = (second % 3600) / 60;
            int second2 = second % 60;

            if (hour < 10) {
                if (minute < 10) {
                    if (second2 < 10) {
                        return "0" + hour + ":0" + minute + ":0" + String.valueOf(second2);
                    } else {
                        return "0" + hour + ":0" + minute + ":" + String.valueOf(second2);
                    }
                } else {
                    if (second2 < 10) {
                        return "0" + hour + ":" + minute + ":0" + String.valueOf(second2);
                    } else {
                        return "0" + hour + ":" + minute + ":" + String.valueOf(second2);
                    }
                }
            } else {
                if (minute < 10) {
                    if (second2 < 10) {
                        return hour + ":0" + minute + ":0" + String.valueOf(second2);
                    } else {
                        return hour + ":0" + minute + ":" + String.valueOf(second2);
                    }
                } else {
                    if (second2 < 10) {
                        return hour + ":" + minute + ":0" + String.valueOf(second2);
                    } else {
                        return hour + ":" + minute + ":" + String.valueOf(second2);
                    }
                }
            }
        }
    }

    public static String formatTimeByChinese(int second) {
        if (second > 24 * 3600) {
            return "超过一天";
        }
        if (second < 60) {
            return second + "秒";
        } else if (second < 60 * 60) {
            int minute = second / 60;
            int second2 = second % 60;
            return minute + "分" + second2 + "秒";
        } else {
            int hour = second / 3600;
            int minute = (second % 3600) / 60;
            int second2 = second % 60;
            return hour + "小时" + minute + "分" + second2 + "秒";
        }
    }

    public static String formatCountByTenThousand(long count, final String TenThousand, final String OneHundredMillion) {
        String res;

        if (count < 10000) {
            res = String.valueOf(count);
        } else if (count >= 10000 && count < 10000 * 10000) {
            res = String.format(TenThousand, count / 10000);
        } else {
            res = String.format(OneHundredMillion, String.format("%.2f", count * 1.0f / (10000.0f * 10000.0f)));
        }

        return res;
    }

    /**
     * 鏍煎紡鍖栨枃浠跺ぇ灏?
     */
    public static String formatFileSize(Context context, long number) {
        return formatFileSize(context, number, false);
    }

    /**
     * @param minUnitIsMB 最小单位是M
     **/
    public static String formatFileSize(Context context, long number, boolean shorter, boolean minUnitIsMB) {
        Pair<String, String> value = formatFileSizePair(number, shorter, minUnitIsMB);
        return value.first + value.second;
    }

    /**
     * 鏍煎紡鍖栨枃浠跺ぇ灏?
     *
     * @param shorter 灏介噺绠?寲鏄剧ず鐨勫瓧绗︼紝濡傦細KB浼氭樉绀烘垚K
     */
    public static String formatFileSize(Context context, long number, boolean shorter) {
        Pair<String, String> value = formatFileSizePair(number, shorter, false);
        return value.first + value.second;
    }

    /**
     * 鏍煎紡鍖栨枃浠跺ぇ灏?
     */
    public static Pair<String, String> formatFileSizePair(long number) {
        return formatFileSizePair(number, false, false);
    }

    private static final String FORMALT_1F = "%.1f";
    private static final String FORMALT_2F = "%.2f";
    private static final String FORMALT_0F = "%.0f";

    /**
     * 鏍煎紡鍖栨枃浠跺ぇ灏忥紝浠庣郴缁烣ormatter.formatFileSize鏂规硶鎷疯礉鑰屾潵锛?
     *
     * @param shorter 灏介噺绠?寲鏄剧ず鐨勫瓧绗︼紝濡傦細KB浼氭樉绀烘垚K锛?
     * @param minUnitIsMB
     */
    public static Pair<String, String> formatFileSizePair(long number, boolean shorter, boolean minUnitIsMB) {
        float result = number;
        String suffix = "B";
        if (result > 900) {
            suffix = shorter ? "K" : "KB";
            result = result / 1024F;
        }
        if (result > 900 || minUnitIsMB) {
            suffix = shorter ? "M" : "MB";
            result = result / 1024F;
        }
        if (result > 900) {
            suffix = shorter ? "G" : "GB";
            result = result / 1024F;
        }
        if (result > 900) {
            suffix = shorter ? "T" : "TB";
            result = result / 1024F;
        }
        if (result > 900) {
            suffix = shorter ? "P" : "PB";
            result = result / 1024F;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 100) {
            value = String.format(shorter ? FORMALT_1F : FORMALT_2F, result);
        } else {
            value = String.format(shorter ? FORMALT_0F : FORMALT_2F, result);
        }
        return new Pair<String, String>(value, suffix);
}

    public static String formatSizeHighAccury(Context context, long size) {
        return Formatter.formatFileSize(context, size).replaceAll("B", "");
    }
}
