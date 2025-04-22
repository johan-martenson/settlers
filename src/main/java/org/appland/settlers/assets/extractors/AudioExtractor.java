package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.GameResource;
import org.appland.settlers.assets.GameResourceType;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.WaveGameResource;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.gamefiles.SoundLst;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.WaveFile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioExtractor {
    private record TitleAndFilename(String title, String filename) { }

    private static final List<TitleAndFilename> MUSIC_TITLE_AND_FILENAMES = new ArrayList<>(List.of(
            new TitleAndFilename("Track 1", "audio/01_-_Track_01.mp3"),
            new TitleAndFilename("Track 2", "audio/02_-_Track_02.mp3"),
            new TitleAndFilename("Track 3", "audio/03_-_Track_03.mp3"),
            new TitleAndFilename("Track 4", "audio/04_-_Track_04.mp3"),
            new TitleAndFilename("Track 5", "audio/05_-_Track_05.mp3"),
            new TitleAndFilename("Track 6", "audio/06_-_Track_06.mp3"),
            new TitleAndFilename("Track 7", "audio/07_-_Track_07.mp3"),
            new TitleAndFilename("Track 8", "audio/08_-_Track_08.mp3")
    ));

    public static void extractAudioAssets(String fromDir, String toDir, Palette defaultPalette) throws IOException, UnknownResourceTypeException, InvalidFormatException {

        // Write the music atlas
        var jsonSongs = new JSONArray();

        for (TitleAndFilename titleAndFilename : MUSIC_TITLE_AND_FILENAMES) {
            JSONObject jsonSong = new JSONObject();

            jsonSong.put("path", toDir + "/" + titleAndFilename.filename);
            jsonSong.put("title", titleAndFilename.title);

            jsonSongs.add(jsonSong);
        }

        // Write the audio atlas for the music
        Files.writeString(Paths.get(toDir, "audio-atlas-music.json"), jsonSongs.toJSONString());

        Files.createDirectory(Paths.get(toDir, "audio"));

        // The music files are converted from XMI to MP3s outside this tool. Tell the user where to place them:
        System.out.println("ACTION: place the music files in audio/song[0-9].mp3");

        // Extract each individual sound
        var gameResources = LstDecoder.loadLstFile(fromDir + "/" + SoundLst.FILENAME, defaultPalette);

        // Save each wave file, so we can try to figure out when they are used
        for (int i = 0; i < gameResources.size(); i++) {
            GameResource gameResource = gameResources.get(i);

            if (gameResource.getType() == GameResourceType.WAVE_SOUND) {
                WaveGameResource waveGameResource = (WaveGameResource) gameResource;
                WaveFile waveFile1 = waveGameResource.getWaveFile();

                waveFile1.writeToFile(toDir + "/wavefile-" + i + ".wave");
            }
        }

        // Write sounds
        Map<String, Integer> sounds = new HashMap<>();

        sounds.put(toDir + "/audio/fire.wave", SoundLst.FIRE);
        sounds.put(toDir + "/audio/fighting-hit-0.wave", SoundLst.FIGHTING_HIT_0);
        sounds.put(toDir + "/audio/fighting-unknown.wave", SoundLst.FIGHTING_UNKNOWN);
        sounds.put(toDir + "/audio/fighting-soldier-died.wave", SoundLst.FIGHTING_SOLDIER_DIED);
        sounds.put(toDir + "/audio/fighting-hit-1.wave", SoundLst.FIGHTING_HIT_1);
        sounds.put(toDir + "/audio/woodcutter-0.wave", SoundLst.SAWMILL_WORKER_0);
        sounds.put(toDir + "/audio/woodcutter-1.wave", SoundLst.SAWMILL_WORKER_1);
        sounds.put(toDir + "/audio/pig-breeder-0.wave", SoundLst.PIG_BREEDER_0);
        sounds.put(toDir + "/audio/pig-breeder-1.wave", SoundLst.PIG_BREEDER_1);
        sounds.put(toDir + "/audio/shipwright-0.wave", SoundLst.SHIPWRIGHT_0);
        sounds.put(toDir + "/audio/shipwright-1.wave", SoundLst.SHIPWRIGHT_1);
        sounds.put(toDir + "/audio/forester-0.wave", SoundLst.FORESTER_0);
        sounds.put(toDir + "/audio/forester-1.wave", SoundLst.FORESTER_1);
        sounds.put(toDir + "/audio/iron-founder-and-brewer.wave", SoundLst.IRON_FOUNDER_AND_BREWER);
        sounds.put(toDir + "/audio/farmer.wave", SoundLst.FARMER);
        sounds.put(toDir + "/audio/miner.wave", SoundLst.MINER);
        sounds.put(toDir + "/audio/geologist-digging-0.wave", SoundLst.GEOLOGIST_DIGGING_0);
        sounds.put(toDir + "/audio/geologist-digging-1.wave", SoundLst.GEOLOGIST_DIGGING_1);

        // TODO: brewer is at 51 which maps where?

        sounds.put(toDir + "/audio/builder-kneeling-hammering.wave", SoundLst.BUILDER_KNEELING_HAMMERING);
        sounds.put(toDir + "/audio/builder-standing-hammering.wave", SoundLst.BUILDER_STANDING_HAMMERING);
        sounds.put(toDir + "/audio/stonemason-hacking.wave", SoundLst.STONEMASON_HACKING);
        sounds.put(toDir + "/audio/miller.wave", SoundLst.MILLER);
        sounds.put(toDir + "/audio/butcher.wave", SoundLst.BUTCHER);
        sounds.put(toDir + "/audio/fisherman.wave", SoundLst.FISHERMAN);
        sounds.put(toDir + "/audio/planer-0.wave", SoundLst.PLANER_0);
        sounds.put(toDir + "/audio/planer-1.wave", SoundLst.PLANER_1);
        sounds.put(toDir + "/audio/woodcutter-cutting.wave", SoundLst.WOODCUTTER_CUTTING);
        sounds.put(toDir + "/audio/falling-tree.wave", SoundLst.FALLING_TREE);
        sounds.put(toDir + "/audio/baker-baking.wave", SoundLst.BAKER);
        sounds.put(toDir + "/audio/hunter.wave", SoundLst.HUNTER);
        sounds.put(toDir + "/audio/metalworker-0.wave", SoundLst.METALWORKER_0);
        sounds.put(toDir + "/audio/metalworker-1.wave", SoundLst.METALWORKER_1);
        sounds.put(toDir + "/audio/metalworker-2.wave", SoundLst.METALWORKER_2);
        sounds.put(toDir + "/audio/armorer.wave", SoundLst.ARMORER);
        sounds.put(toDir + "/audio/courier-with-boat.wave", SoundLst.COURIER_WITH_BOAT);
        sounds.put(toDir + "/audio/well-worker.wave", SoundLst.WELL_WORKER);

        sounds.put(toDir + "/audio/duck-quack.wave", SoundLst.DUCK_QUACK);
        sounds.put(toDir + "/audio/geologist-finding.wave", SoundLst.GEOLOGIST_FOUND_ORE);
        sounds.put(toDir + "/audio/new-message.wave", SoundLst.MILITARY_BUILDING_OCCUPIED);
        sounds.put(toDir + "/audio/new-message.wave", SoundLst.NEW_MESSAGE);

        // TODO: add additional sounds
        // TODO: verify that the offset remains correct. Brewer has sound 51?
        sounds.forEach((path, value) -> ((WaveGameResource) gameResources.get(value)).getWaveFile().writeToFile(path));
    }
}
