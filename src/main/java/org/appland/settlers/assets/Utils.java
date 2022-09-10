/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.assets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author johan
 */
public class Utils {

    public static short getUint8FromByteArray(byte[] arr, int i) {
        ByteBuffer bb = ByteBuffer.wrap(arr);

        return (short)(bb.get(i) & 0xff);
    }

    static String getHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();

        for (byte b : bytes) {
            hex.append(Integer.toHexString(b & 0xff));
        }

        return hex.toString();
    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    private static void printUnsigned8BitAsHex(byte b) {

        if (b < 10) {
            System.out.print(b);
        } else {

            switch (b) {
                case 10:
                    System.out.print("A");
                    break;
                case 11:
                    System.out.print("B");
                    break;
                case 12:
                    System.out.print("C");
                    break;
                case 13:
                    System.out.print("D");
                    break;
                case 14:
                    System.out.print("E");
                    break;
                case 15:
                    System.out.print("F");
                    break;
                default:
                    throw new RuntimeException("Failed assumption when printing as hex: " + b);
            }
        }
    }

    static void printAsHexString(byte[] bytes) {

        System.out.print("0x");

        for (byte b : bytes) {
            byte lower = (byte) (b & 0x0F);
            byte upper = (byte) ((b & 0xF0) >> 4);

            printUnsigned8BitAsHex(upper);
            printUnsigned8BitAsHex(lower);

            System.out.print(" ");
        }

        System.out.println();
    }

    public static boolean isValidGameDirectory(String fromDir) throws IOException {
        Path fromDirPath = Paths.get(fromDir);

        if (!Files.isDirectory(fromDirPath)) {
            System.out.println("Not a directory!");

            return false;
        }

        Set<String> children = Files.walk(fromDirPath, 1, FileVisitOption.FOLLOW_LINKS).map(path -> path.getFileName().toString()).collect(Collectors.toSet());

        if (!children.contains("BLUEBYTE")) {
            System.out.println("No bluebyte child!");

            System.out.println(children);

            return false;
        }

        return true;
    }

    public static boolean isDirectory(String path) {
        System.out.println("Is dir: " + Files.isDirectory(Paths.get(path)));

        return Files.isDirectory(Paths.get(path));
    }

    public static boolean isEmptyDirectory(String toDir) throws IOException {
        Path toDirPath = Paths.get(toDir);

        if (!Files.isDirectory(toDirPath)) {
            System.out.println("Is not dir");

            return false;
        }

        Set<Path> children = Files.walk(toDirPath, 1).filter(p -> !p.equals(toDirPath)).collect(Collectors.toSet());

        if (!children.isEmpty()) {
            System.out.println("Is not empty");
            System.out.println(children);

            return false;
        }

        System.out.println(children);

        return true;
    }

    public static void createDirectory(String dirToCreate) throws IOException {
        Files.createDirectory(Paths.get(dirToCreate));
    }

    public static String nullTerminatedByteArrayToString(byte[] bytes) {
        byte[] cleanedBytes = new byte[bytes.length];
        int to = 0;

        for (int from = 0; from < bytes.length; from++) {
            if (bytes[from] == '\0') {
                break;
            }

            if (bytes[from] == '\r' && from < bytes.length - 1) {
                continue;
            }

            if (bytes[from] < 32 && bytes[from] != '\n') {
                continue;
            }

            cleanedBytes[to] = bytes[from];

            to = to + 1;
        }

        return new String(cleanedBytes, 0, to, StandardCharsets.US_ASCII);
    }

    public static Bitmap getBitmapFromGameResource(GameResource gameResource) {
        switch (gameResource.getType()) {
            case BITMAP_RESOURCE:
                BitmapResource bitmapResource = (BitmapResource)gameResource;

                return bitmapResource.getBitmap();

            case BITMAP_RLE:
                BitmapRLEResource bitmapRLEResource = (BitmapRLEResource) gameResource;

                return bitmapRLEResource.getBitmap();

            case PLAYER_BITMAP_RESOURCE:
                PlayerBitmapResource playerBitmapResource = (PlayerBitmapResource) gameResource;

                return playerBitmapResource.getBitmap();

            default:
                throw new RuntimeException("Cannot get bitmap for: " + gameResource.getType());

        }
    }

    public static int max(int... numbers) {
        int result = numbers[0];

        for (int number : numbers) {
            result = Math.max(result, number);
        }

        return result;
    }

    public static List<Bitmap> mirrorImageSeries(List<Bitmap> images) {
        List<Bitmap> mirroredSeries = new ArrayList<>();

        for (Bitmap image : images) {
            Bitmap mirroredImage = image.getMirror();

            mirroredSeries.add(mirroredImage);
        }

        return mirroredSeries;
    }
}
