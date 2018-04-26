package com.zenwherk.api.util;

import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class FileUtilities {

    public static File decodeBase64(String sourceData, String extension) throws Exception  {

        // tokenize the data
        StringBuilder strBuilder = new StringBuilder(sourceData);
        int dataIndex = strBuilder.indexOf(",");
        String imageString;
        if(dataIndex < 0) {
            imageString = strBuilder.toString();
        } else {
            imageString = strBuilder.substring(dataIndex + 1);
        }

        // create a buffered image
        BufferedImage image = null;
        byte[] imageByte;

        BASE64Decoder decoder = new BASE64Decoder();
        imageByte = decoder.decodeBuffer(imageString);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        image = ImageIO.read(bis);
        bis.close();

        // write the image to a file
        File outputFile = new File(String.format("./src/main/tmp/image.%s", extension));
        ImageIO.write(image, extension, outputFile);
        return outputFile;
    }
}
