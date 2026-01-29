/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.assets;

import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.assets.utils.Animation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class for various operations including file handling and data conversion.
 */
public class Utils {
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    /**
     * Reads an unsigned byte from a byte array at the specified index.
     *
     * @param arr The byte array.
     * @param i The index.
     * @return The unsigned byte value.
     */
    public static short getUint8FromByteArray(byte[] arr, int i) {
        return (short) (arr[i] & 0xff);
    }

    static String getHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();

        for (byte b : bytes) {
            hex.append(Integer.toHexString(b & 0xff));
        }

        return hex.toString();
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes The byte array.
     * @return The hexadecimal string representation.
     */
    public static String convertBytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    private static void printUnsigned8BitAsHex(byte b) {
        switch (b) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 -> System.out.print(b);
            case 10 -> System.out.print("A");
            case 11 -> System.out.print("B");
            case 12 -> System.out.print("C");
            case 13 -> System.out.print("D");
            case 14 -> System.out.print("E");
            case 15 -> System.out.print("F");
            default -> throw new RuntimeException(String.format("Failed assumption when printing as hex: %d", b));
        }
    }

    /**
     * Prints a byte array as a hexadecimal string.
     *
     * @param bytes The byte array.
     */
    static void printBytesAsHex(byte[] bytes) {
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

    /**
     * Validates if a directory contains the "BLUEBYTE" child directory.
     *
     * @param fromDir The directory path.
     * @return True if valid, otherwise false.
     * @throws IOException If an I/O error occurs.
     */
    public static boolean isValidGameDirectory(String fromDir) throws IOException {
        Path fromDirPath = Paths.get(fromDir);

        if (!Files.isDirectory(fromDirPath)) {
            System.out.println("Not a directory!");

            return false;
        }

        Set<String> children = Files.walk(fromDirPath, 1, FileVisitOption.FOLLOW_LINKS)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toSet());

        if (!children.contains("BLUEBYTE")) {
            System.out.println("No bluebyte child!");
            System.out.println(children);

            return false;
        }

        return true;
    }

    /**
     * Checks if the given path is a directory.
     *
     * @param path The directory path.
     * @return True if it's a directory, otherwise false.
     */
    public static boolean isDirectory(String path) {
        boolean isDirectory = Files.isDirectory(Paths.get(path));
        System.out.printf("Is dir: %b%n", isDirectory);
        return isDirectory;
    }

    /**
     * Checks if the given directory is empty.
     *
     * @param toDir The directory path.
     * @return True if the directory is empty, otherwise false.
     * @throws IOException If an I/O error occurs.
     */
    public static boolean isEmptyDirectory(String toDir) throws IOException {
        Path toDirPath = Paths.get(toDir);

        if (!Files.isDirectory(toDirPath)) {
            System.out.println("Is not dir");

            return false;
        }

        Set<Path> children = Files.walk(toDirPath, 1)
                .filter(p -> !p.equals(toDirPath))
                .collect(Collectors.toSet());

        if (!children.isEmpty()) {
            System.out.println("Is not empty");
            System.out.println(children);

            return false;
        }

        System.out.println(children);

        return true;
    }

    /**
     * Creates a directory at the specified path.
     *
     * @param dirToCreate The directory path.
     * @throws IOException If an I/O error occurs.
     */
    public static void createDirectory(String dirToCreate) throws IOException {
        Files.createDirectory(Paths.get(dirToCreate));
    }

    /**
     * Converts a null-terminated byte array to a string.
     *
     * @param bytes The byte array.
     * @return The resulting string.
     */
    public static String convertNullTerminatedBytesToString(byte[] bytes) {
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

            to++;
        }

        return new String(cleanedBytes, 0, to, StandardCharsets.US_ASCII);
    }

    /**
     * Retrieves the Bitmap from the given GameResource.
     *
     * @param gameResource The game resource.
     * @return The corresponding Bitmap.
     */
    public static Bitmap getBitmapFromGameResource(GameResource gameResource) {
        return switch (gameResource.getType()) {
            case BITMAP_RESOURCE -> ((BitmapResource) gameResource).getBitmap();
            case BITMAP_RLE -> ((BitmapRLEResource) gameResource).getBitmap();
            case PLAYER_BITMAP_RESOURCE -> ((PlayerBitmapResource) gameResource).getBitmap();
            case LBM_RESOURCE -> ((LBMGameResource) gameResource).getLbmFile();
            default -> throw new RuntimeException(String.format("Cannot get bitmap for: %s", gameResource.getType()));
        };
    }

    /**
     * Finds the maximum value among the given numbers.
     *
     * @param numbers The numbers to compare.
     * @return The maximum value.
     */
    public static int max(int... numbers) {
        return Arrays.stream(numbers).max().orElseThrow(() -> new RuntimeException("No numbers provided"));
    }

    /**
     * Retrieves a list of PlayerBitmap images from game resources starting at a specific index.
     *
     * @param gameResources The list of game resources.
     * @param start The starting index.
     * @param amount The number of images to retrieve.
     * @return The list of PlayerBitmap images.
     */
    public static List<PlayerBitmap> getPlayerImagesAt(List<GameResource> gameResources, int start, int amount) {
        return IntStream.range(start, start + amount)
                .mapToObj(i -> getPlayerImageAt(gameResources, i))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of Bitmap images from game resources starting at a specific index.
     *
     * @param gameResourceList The list of game resources.
     * @param startLocation The starting index.
     * @param amount The number of images to retrieve.
     * @return The list of Bitmap images.
     */
    public static List<Bitmap> getImagesAt(List<GameResource> gameResourceList, int startLocation, int amount) {
        return IntStream.range(startLocation, startLocation + amount)
                .mapToObj(i -> getImageAt(gameResourceList, i))
                .collect(Collectors.toList());
    }

    public static List<Bitmap> getImagesAt(List<GameResource> gameResources, Animation animation) {
        return getImagesAt(gameResources, animation.index(), animation.length());
    }

    /**
     * Retrieves a PlayerBitmap image from the game resources at a specific location.
     *
     * @param gameResourceList The list of game resources.
     * @param location The index location.
     * @return The PlayerBitmap image.
     */
    public static PlayerBitmap getPlayerImageAt(List<GameResource> gameResourceList, int location) {
        return ((PlayerBitmapResource) gameResourceList.get(location)).getBitmap();
    }

    /**
     * Retrieves a Bitmap image from the game resources at a specific location.
     *
     * @param gameResourceList The list of game resources.
     * @param location The index location.
     * @return The Bitmap image.
     */
    public static Bitmap getImageAt(List<GameResource> gameResourceList, int location) {
        GameResource gameResource = gameResourceList.get(location);

        return getBitmapFromGameResource(gameResource);
    }
}
