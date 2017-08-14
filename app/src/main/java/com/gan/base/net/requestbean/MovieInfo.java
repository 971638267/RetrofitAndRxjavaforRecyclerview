package com.gan.base.net.requestbean;

/**
 * Created by gan on 2017/5/18.
 */

public class MovieInfo {
    public MovieInfo() {
    }

    public String year;
    public ImageInfo images;
    public String title;

    public class ImageInfo {
        public ImageInfo() {
        }

        public String small;
        public String large;
        public String medium;

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ImageInfo{");
            sb.append("small='").append(small).append('\'');
            sb.append(", large='").append(large).append('\'');
            sb.append(", medium='").append(medium).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MovieInfo{");
        sb.append("year='").append(year).append('\'');
        sb.append(", images=").append(images);
        sb.append(", title='").append(title).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
