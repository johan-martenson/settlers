package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.BobResource;
import org.appland.settlers.assets.CarrierCargo;
import org.appland.settlers.assets.CompassDirection;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.JobType;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.TextureFormat;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.WorkerDetails;
import org.appland.settlers.assets.collectors.WorkerImageCollection;
import org.appland.settlers.assets.decoders.BobDecoder;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.gamefiles.CarrierBob;
import org.appland.settlers.assets.gamefiles.CbobRomBobsLst;
import org.appland.settlers.assets.gamefiles.JobsBob;
import org.appland.settlers.assets.gamefiles.Map0ZLst;
import org.appland.settlers.assets.resources.Bitmap;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.WorkerAction;

import java.awt.Point;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Set;

import static java.lang.String.format;
import static org.appland.settlers.assets.CompassDirection.*;
import static org.appland.settlers.assets.Nation.*;
import static org.appland.settlers.assets.gamefiles.JobsBob.*;
import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.WorkerAction.*;
import static org.appland.settlers.model.actors.Courier.BodyType.FAT;
import static org.appland.settlers.model.actors.Courier.BodyType.THIN;

public class WorkersExtractor {
    static final Set<JobType> nationSpecificWorkers = Set.of(
            JobType.PRIVATE,
            JobType.PRIVATE_FIRST_CLASS,
            JobType.SERGEANT,
            JobType.OFFICER,
            JobType.GENERAL);

    public static void extractWorkerAssets(String fromDir, String toDir, Palette defaultPalette) throws IOException, UnknownResourceTypeException, InvalidFormatException {
        var jobsBobList = LstDecoder.loadLstFile(format("%s/%s",fromDir, JobsBob.FILENAME), defaultPalette);
        var map0ZLst = LstDecoder.loadLstFile(format("%s/%s", fromDir, Map0ZLst.FILENAME), defaultPalette);
        var cbobRomBobsLst = LstDecoder.loadLstFile(format("%s/%s", fromDir, CbobRomBobsLst.FILENAME), defaultPalette);
        var jobsBobResource = (BobResource) jobsBobList.getFirst();
        var workerDetailsMap = new EnumMap<JobType, WorkerDetails>(JobType.class);

        // FIXME: assume RANGER == FORESTER

        /*
         * Translate ids:
         *  - 0 (Africans) -> 3
         *  - 1 (Japanese) -> 2
         *  - 2 (Romans)   -> 0
         *  - 3 (Vikings)  -> 1
         * */

        // Construct the worker details map
        workerDetailsMap.put(JobType.HELPER, new WorkerDetails(false, JobsBob.HELPER_BOB_ID));
        workerDetailsMap.put(JobType.WOODCUTTER, WOODCUTTER_BOB);
        workerDetailsMap.put(JobType.FISHER, new WorkerDetails(false, JobsBob.FISHERMAN_BOB_ID));
        workerDetailsMap.put(JobType.FORESTER, new WorkerDetails(false, JobsBob.FORESTER_BOB_ID));
        workerDetailsMap.put(JobType.CARPENTER, new WorkerDetails(false, JobsBob.CARPENTER_BOB_ID));
        workerDetailsMap.put(JobType.STONEMASON, new WorkerDetails(false, JobsBob.STONEMASON_BOB_ID));
        workerDetailsMap.put(JobType.HUNTER, new WorkerDetails(false, JobsBob.HUNTER_BOB_ID));
        workerDetailsMap.put(JobType.FARMER, new WorkerDetails(false, JobsBob.FARMER_BOB_ID));
        workerDetailsMap.put(JobType.MILLER, new WorkerDetails(true, JobsBob.MILLER_BOB_ID));
        workerDetailsMap.put(JobType.BAKER, new WorkerDetails(true, JobsBob.BAKER_BOB_ID));
        workerDetailsMap.put(JobType.BUTCHER, new WorkerDetails(false, JobsBob.BUTCHER_BOB_ID));
        workerDetailsMap.put(JobType.MINER, new WorkerDetails(false, JobsBob.MINER_BOB_ID));
        workerDetailsMap.put(JobType.BREWER, new WorkerDetails(true, JobsBob.BREWER_BOB_ID));
        workerDetailsMap.put(JobType.PIG_BREEDER, new WorkerDetails(false, JobsBob.PIG_BREEDER_BOB_ID));
        workerDetailsMap.put(JobType.DONKEY_BREEDER, new WorkerDetails(false, JobsBob.DONKEY_BREEDER_BOB_ID));
        workerDetailsMap.put(JobType.IRON_FOUNDER, new WorkerDetails(false, JobsBob.IRON_FOUNDER_BOB_ID));
        workerDetailsMap.put(JobType.MINTER, new WorkerDetails(false, JobsBob.MINTER_BOB_ID));
        workerDetailsMap.put(JobType.METALWORKER, new WorkerDetails(false, JobsBob.METALWORKER_BOB_ID));
        workerDetailsMap.put(JobType.ARMORER, new WorkerDetails(true, JobsBob.ARMORER_BOB_ID));
        workerDetailsMap.put(JobType.BUILDER, new WorkerDetails(false, JobsBob.BUILDER_BOB_ID));
        workerDetailsMap.put(JobType.PLANER, new WorkerDetails(false, JobsBob.PLANER_BOB_ID));
        workerDetailsMap.put(JobType.PRIVATE, new WorkerDetails(false, JobsBob.PRIVATE_BOB_ID));
        workerDetailsMap.put(JobType.PRIVATE_FIRST_CLASS, new WorkerDetails(false, JobsBob.PRIVATE_FIRST_CLASS_BOB_ID));
        workerDetailsMap.put(JobType.SERGEANT, new WorkerDetails(false, JobsBob.SERGEANT_BOB_ID));
        workerDetailsMap.put(JobType.OFFICER, new WorkerDetails(false, JobsBob.OFFICER_BOB_ID));
        workerDetailsMap.put(JobType.GENERAL, new WorkerDetails(false, JobsBob.GENERAL_BOB_ID));
        workerDetailsMap.put(JobType.GEOLOGIST, new WorkerDetails(false, JobsBob.GEOLOGIST_BOB_ID));
        workerDetailsMap.put(JobType.SHIP_WRIGHT, new WorkerDetails(false, JobsBob.SHIP_WRIGHT_BOB_ID));
        workerDetailsMap.put(JobType.SCOUT, new WorkerDetails(false, JobsBob.SCOUT_BOB_ID));
        workerDetailsMap.put(JobType.PACK_DONKEY, new WorkerDetails(false, JobsBob.PACK_DONKEY_BOB_ID));
        workerDetailsMap.put(JobType.BOAT_CARRIER, new WorkerDetails(false, JobsBob.BOAT_CARRIER_BOB_ID));

        // Compose the worker images and animations
        var renderedWorkers = BobDecoder.renderWorkerImages(jobsBobResource.getBob(), workerDetailsMap);
        var workerCollectors = new EnumMap<JobType, WorkerImageCollection>(JobType.class);

        for (var jobType : JobType.values()) {
            var renderedWorker = renderedWorkers.get(jobType);
            var workerImageCollection = new WorkerImageCollection(jobType.name().toLowerCase());

            for (var nation : Nation.values()) {
                for (var direction : CompassDirection.values()) {
                    var stackedBitmaps = renderedWorker.getAnimation(nation, direction);

                    for (var frame : stackedBitmaps) {
                        var body = frame.getPlayerBitmaps().getFirst();
                        var head = frame.getPlayerBitmaps().get(1);

                        // Calculate the dimension
                        var maxOrigin = new Point(0, 0);

                        if (!frame.getPlayerBitmaps().isEmpty()) {
                            maxOrigin.x = Integer.MIN_VALUE;
                            maxOrigin.y = Integer.MIN_VALUE;

                            boolean hasPlayerColor = false;

                            for (var bitmap : frame.getPlayerBitmaps()) {
                                hasPlayerColor = true;

                                var bitmapVisibleArea = bitmap.getVisibleArea();
                                var bitmapOrigin = bitmap.getOffsetsForVisibleImage();

                                maxOrigin.x = Math.max(maxOrigin.x, bitmapOrigin.x);
                                maxOrigin.y = Math.max(maxOrigin.y, bitmapOrigin.y);

                                maxOrigin.x = Math.max(maxOrigin.x, bitmapVisibleArea.width() - bitmapOrigin.x);
                                maxOrigin.y = Math.max(maxOrigin.y, bitmapVisibleArea.height() - bitmapOrigin.y);
                            }

                            for (var playerColor : PlayerColor.values()) {

                                // Create a bitmap to merge both body and head into
                                var merged = mergeBodyAndHead(body, head, playerColor, hasPlayerColor, maxOrigin, defaultPalette);

                                // Store the image in the worker image collection
                                if (nationSpecificWorkers.contains(jobType)) {
                                    if (hasPlayerColor) {
                                        System.out.println("Adding " + jobType + nation + ", " + playerColor + ", " + direction);

                                        workerImageCollection.addNationSpecificImageWithPlayerColor(nation, playerColor, direction, merged);
                                    } else {
                                        System.out.println("Adding " + jobType + nation + ", " + ", " + direction);

                                        workerImageCollection.addNationSpecificImage(nation, direction, merged);
                                    }
                                } else {
                                    if (hasPlayerColor) {
                                        workerImageCollection.addImageWithPlayerColor(playerColor, direction, merged);
                                    } else {
                                        workerImageCollection.addImage(direction, merged);
                                    }
                                }

                                if (!hasPlayerColor) {
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!nationSpecificWorkers.contains(jobType)) {
                    break;
                }
            }

            workerImageCollection.addShadowImages(EAST, map0ZLst, Map0ZLst.WALKING_EAST_SHADOW);
            workerImageCollection.addShadowImages(SOUTH_EAST, map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW);
            workerImageCollection.addShadowImages(SOUTH_WEST, map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW);
            workerImageCollection.addShadowImages(WEST, map0ZLst, Map0ZLst.WALKING_WEST_SHADOW);
            workerImageCollection.addShadowImages(NORTH_WEST, map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW);
            workerImageCollection.addShadowImages(NORTH_EAST, map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW);

            // Store the worker image collector
            workerCollectors.put(jobType, workerImageCollection);
        }

        // Add cargo carrying images and animations
        var helperCollector = workerCollectors.get(JobType.HELPER);
        var woodcutterCollector = workerCollectors.get(JobType.WOODCUTTER);
        var carpenterCollector = workerCollectors.get(JobType.CARPENTER);
        var fishermanCollector = workerCollectors.get(JobType.FISHER);
        var stonemasonCollector = workerCollectors.get(JobType.STONEMASON);
        var minterCollector = workerCollectors.get(JobType.MINTER);
        var minerCollector = workerCollectors.get(JobType.MINER);
        var farmerCollector = workerCollectors.get(JobType.FARMER);
        var pigBreederCollector = workerCollectors.get(JobType.PIG_BREEDER);
        var millerCollector = workerCollectors.get(JobType.MILLER);
        var bakerCollector = workerCollectors.get(JobType.BAKER);
        var metalWorkerCollector = workerCollectors.get(JobType.METALWORKER);
        var butcherWorkerCollector = workerCollectors.get(JobType.BUTCHER);
        var ironFounderCollector = workerCollectors.get(JobType.IRON_FOUNDER);
        var hunterCollector = workerCollectors.get(JobType.HUNTER);
        var shipwrightCollector = workerCollectors.get(JobType.SHIP_WRIGHT);
        var brewerCollector = workerCollectors.get(JobType.BREWER);
        var armorerCollector = workerCollectors.get(JobType.ARMORER);
        var foresterCollector = workerCollectors.get(JobType.FORESTER);
        var planerCollector = workerCollectors.get(JobType.PLANER);
        var geologistCollector = workerCollectors.get(JobType.GEOLOGIST);
        var builderCollector = workerCollectors.get(JobType.BUILDER);
        var privateCollector = workerCollectors.get(JobType.PRIVATE);
        var privateFirstClassCollector = workerCollectors.get(JobType.PRIVATE_FIRST_CLASS);
        var sergeantCollector = workerCollectors.get(JobType.SERGEANT);
        var officerCollector = workerCollectors.get(JobType.OFFICER);
        var generalCollector = workerCollectors.get(JobType.GENERAL);

        var bob = jobsBobResource.getBob();

        woodcutterCollector.readCargoImagesFromBob(WOOD, WOODCUTTER_BOB.getBodyType(), WOODCUTTER_WITH_WOOD_CARGO_BOB_ID, bob);
        woodcutterCollector.addAnimation(WorkerAction.CUTTING, cbobRomBobsLst, CbobRomBobsLst.CUTTING_TREE);

        // Add roman military attacking
        privateCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_ATTACKING_EAST);
        privateCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_ATTACKING_WEST);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_ATTACKING_EAST);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_ATTACKING_WEST);
        sergeantCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_ATTACKING_EAST);
        sergeantCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_ATTACKING_WEST);
        officerCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_ATTACKING_EAST);
        officerCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_ATTACKING_WEST);
        generalCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_ATTACKING_EAST);
        generalCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_ATTACKING_WEST);

        // Add roman military getting hit
        privateCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_EAST_SHIELD_UP);
        privateCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_WEST_SHIELD_UP);
        privateCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_EAST_STAND_ASIDE);
        privateCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_WEST_STAND_ASIDE);
        privateCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_EAST_JUMP_BACK);
        privateCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_WEST_JUMP_BACK);

        privateFirstClassCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_EAST_JUMPING_BACK);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_WEST_JUMPING_BACK);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_EAST_FLINCH_HIT);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_WEST_FLINCH_HIT);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_EAST_GETTING_HIT);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_WEST_GETTING_HIT);

        sergeantCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_EAST_AVOIDING_HIT);
        sergeantCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_WEST_AVOIDING_HIT);
        sergeantCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_EAST_FLINCH_HIT);
        sergeantCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_WEST_FLINCH_HIT);
        sergeantCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_EAST_GETTING_HIT);
        sergeantCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_WEST_GETTING_HIT);

        officerCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_EAST_AVOIDING_HIT);
        officerCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_WEST_AVOIDING_HIT);
        officerCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_EAST_FLINCH_HIT);
        officerCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_WEST_FLINCH_HIT);
        officerCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_EAST_GETTING_HIT);
        officerCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_WEST_GETTING_HIT);

        generalCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_EAST_AVOIDING_HIT);
        generalCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_WEST_AVOIDING_HIT);
        generalCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_EAST_FLINCH_HIT);
        generalCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_WEST_FLINCH_HIT);
        generalCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_EAST_GETTING_HIT);
        generalCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_WEST_GETTING_HIT);

        privateCollector.addAnimation(DIE, cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING);
        privateFirstClassCollector.addAnimation(DIE, cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING);
        sergeantCollector.addAnimation(DIE, cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING);
        officerCollector.addAnimation(DIE, cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING);
        generalCollector.addAnimation(DIE, cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING);

        // Add Japanese soldiers attacking
        privateCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_EAST_ATTACKING);
        privateCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_WEST_ATTACKING);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_EAST_ATTACKING);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_WEST_ATTACKING);
        sergeantCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_EAST_ATTACKING);
        sergeantCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_WEST_ATTACKING);
        officerCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_EAST_ATTACKING);
        officerCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_WEST_ATTACKING);
        generalCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_EAST_ATTACKING);
        generalCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_WEST_ATTACKING);

        // Add other actions for Japanese soldiers
        privateCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_EAST_JUMPING_BACK);
        privateCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_WEST_JUMPING_BACK);
        privateCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_EAST_STAND_ASIDE);
        privateCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_WEST_STAND_ASIDE);
        privateCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_EAST_SHIELD_UP);
        privateCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_WEST_SHIELD_UP);

        privateFirstClassCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_EAST_JUMPING_BACK);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_WEST_JUMPING_BACK);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_EAST_STAND_ASIDE);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_WEST_STAND_ASIDE);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_EAST_SHIELD_UP);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_WEST_SHIELD_UP);

        sergeantCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_EAST_JUMPING_BACK);
        sergeantCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_WEST_JUMPING_BACK);
        sergeantCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_EAST_STAND_ASIDE);
        sergeantCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_WEST_STAND_ASIDE);
        sergeantCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_EAST_SHIELD_UP);
        sergeantCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_WEST_SHIELD_UP);

        officerCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_EAST_JUMPING_BACK);
        officerCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_WEST_JUMPING_BACK);
        officerCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_EAST_STAND_ASIDE);
        officerCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_WEST_STAND_ASIDE);
        officerCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_EAST_SHIELD_UP);
        officerCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_WEST_SHIELD_UP);

        generalCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_EAST_JUMPING_BACK);
        generalCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_WEST_JUMPING_BACK);
        generalCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_EAST_STAND_ASIDE);
        generalCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_WEST_STAND_ASIDE);
        generalCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_EAST_SHIELD_UP);
        generalCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_WEST_SHIELD_UP);


        // Add Viking soldiers attacking
        privateCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_EAST_ATTACKING);
        privateCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_WEST_ATTACKING);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_EAST_ATTACKING);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_WEST_ATTACKING);
        sergeantCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_EAST_ATTACKING);
        sergeantCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_WEST_ATTACKING);
        officerCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_EAST_ATTACKING);
        officerCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_WEST_ATTACKING);
        generalCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_EAST_ATTACKING);
        generalCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_WEST_ATTACKING);

        // Add other actions for Viking soldiers
        privateCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_EAST_JUMPING_BACK);
        privateCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_WEST_JUMPING_BACK);
        privateCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_EAST_STAND_ASIDE);
        privateCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_WEST_STAND_ASIDE);
        privateCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_EAST_SHIELD_UP);
        privateCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_WEST_SHIELD_UP);

        privateFirstClassCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_EAST_JUMPING_BACK);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_WEST_JUMPING_BACK);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_EAST_STAND_ASIDE);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_WEST_STAND_ASIDE);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_EAST_SHIELD_UP);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_WEST_SHIELD_UP);

        sergeantCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_EAST_JUMPING_BACK);
        sergeantCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_WEST_JUMPING_BACK);
        sergeantCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_EAST_STAND_ASIDE);
        sergeantCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_WEST_STAND_ASIDE);
        sergeantCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_EAST_SHIELD_UP);
        sergeantCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_WEST_SHIELD_UP);

        officerCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_EAST_JUMPING_BACK);
        officerCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_WEST_JUMPING_BACK);
        officerCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_EAST_STAND_ASIDE);
        officerCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_WEST_STAND_ASIDE);
        officerCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_EAST_SHIELD_UP);
        officerCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_WEST_SHIELD_UP);

        generalCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_EAST_JUMPING_BACK);
        generalCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_WEST_JUMPING_BACK);
        generalCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_EAST_STAND_ASIDE);
        generalCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_WEST_STAND_ASIDE);
        generalCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_EAST_SHIELD_UP);
        generalCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_WEST_SHIELD_UP);


        // Add African soldiers attacking
        privateCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_EAST_ATTACKING);
        privateCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_WEST_ATTACKING);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_EAST_ATTACKING);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_WEST_ATTACKING);
        sergeantCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_EAST_ATTACKING);
        sergeantCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_WEST_ATTACKING);
        officerCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_EAST_ATTACKING);
        officerCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_WEST_ATTACKING);
        generalCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_EAST_ATTACKING);
        generalCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_WEST_ATTACKING);

        // Add other actions for African soldiers
        privateCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_EAST_JUMPING_BACK);
        privateCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_WEST_JUMPING_BACK);
        privateCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_EAST_STAND_ASIDE);
        privateCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_WEST_STAND_ASIDE);
        privateCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_EAST_SHIELD_UP);
        privateCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_WEST_SHIELD_UP);

        privateFirstClassCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_EAST_JUMPING_BACK);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_WEST_JUMPING_BACK);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_EAST_STAND_ASIDE);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_WEST_STAND_ASIDE);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_EAST_SHIELD_UP);
        privateFirstClassCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_WEST_SHIELD_UP);

        sergeantCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_EAST_JUMPING_BACK);
        sergeantCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_WEST_JUMPING_BACK);
        sergeantCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_EAST_STAND_ASIDE);
        sergeantCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_WEST_STAND_ASIDE);
        sergeantCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_EAST_SHIELD_UP);
        sergeantCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_WEST_SHIELD_UP);

        officerCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_EAST_JUMPING_BACK);
        officerCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_WEST_JUMPING_BACK);
        officerCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_EAST_STAND_ASIDE);
        officerCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_WEST_STAND_ASIDE);
        officerCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_EAST_SHIELD_UP);
        officerCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_WEST_SHIELD_UP);

        generalCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_EAST_JUMPING_BACK);
        generalCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_WEST_JUMPING_BACK);
        generalCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_EAST_STAND_ASIDE);
        generalCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_WEST_STAND_ASIDE);
        generalCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_EAST_SHIELD_UP);
        generalCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_WEST_SHIELD_UP);


        // Add regular worker animations
        carpenterCollector.readCargoImagesFromBob(PLANK, CARPENTER_BOB.getBodyType(), CARPENTER_WITH_PLANK_BOB_ID, bob);

        carpenterCollector.addAnimation(WorkerAction.SAWING, cbobRomBobsLst, CbobRomBobsLst.SAWING);

        stonemasonCollector.readCargoImagesFromBob(STONE, STONEMASON_BOB.getBodyType(), STONEMASON_WITH_STONE_CARGO_BOB_ID, bob);

        stonemasonCollector.addAnimation(WorkerAction.HACKING_STONE, cbobRomBobsLst, CbobRomBobsLst.HACKING_STONE);
        foresterCollector.addAnimation(WorkerAction.PLANTING_TREE, cbobRomBobsLst, CbobRomBobsLst.DIGGING_AND_PLANTING);
        planerCollector.addAnimation(WorkerAction.DIGGING_AND_STOMPING, cbobRomBobsLst, CbobRomBobsLst.DIGGING_AND_STOMPING);
        geologistCollector.addAnimation(WorkerAction.INVESTIGATING, cbobRomBobsLst, CbobRomBobsLst.INVESTIGATING);
        builderCollector.addAnimation(WorkerAction.HAMMERING_HOUSE_HIGH_AND_LOW, cbobRomBobsLst, CbobRomBobsLst.HAMMERING_HOUSE_HIGH_AND_LOW);
        builderCollector.addAnimation(WorkerAction.INSPECTING_HOUSE_CONSTRUCTION, cbobRomBobsLst, CbobRomBobsLst.INSPECTING_HOUSE_CONSTRUCTION);

        minterCollector.readCargoImagesFromBob(COIN, MINTER_BOB.getBodyType(), MINTER_WITH_COIN_CARGO_BOB_ID, bob);

        metalWorkerCollector.addAnimation(WorkerAction.HAMMER_TO_MAKE_TOOL, cbobRomBobsLst, CbobRomBobsLst.HAMMER_TO_MAKE_TOOL);
        metalWorkerCollector.addAnimation(WorkerAction.SAWING_TO_MAKE_TOOL, cbobRomBobsLst, CbobRomBobsLst.SAWING_TO_MAKE_TOOL);
        metalWorkerCollector.addAnimation(WorkerAction.WIPE_OFF_SWEAT_TO_MAKE_TOOL, cbobRomBobsLst, CbobRomBobsLst.WIPE_OFF_SWEAT_TO_MAKE_TOOL);

        butcherWorkerCollector.addAnimation(WorkerAction.SLAUGHTERING, cbobRomBobsLst, CbobRomBobsLst.SLAUGHTERING);

        // TODO: add work animation for minter

        minerCollector.readCargoImagesFromBob(GOLD, MINER_BOB.getBodyType(), MINER_WITH_GOLD_CARGO_BOB_ID, bob);
        minerCollector.readCargoImagesFromBob(IRON, MINER_BOB.getBodyType(), MINER_WITH_IRON_CARGO_BOB_ID, bob);
        minerCollector.readCargoImagesFromBob(COAL, MINER_BOB.getBodyType(), MINER_WITH_COAL_CARGO_BOB_ID, bob);
        minerCollector.readCargoImagesFromBob(STONE, MINER_BOB.getBodyType(), MINER_WITH_STONE_CARGO_BOB_ID, bob);

        // TODO: add work animation for miner

        // TODO: job id 69 == carrying crucible/anvil?

        helperCollector.addAnimation(WorkerAction.DRAW_WATER_1, cbobRomBobsLst, CbobRomBobsLst.DRAWING_WATER_1);
        helperCollector.addAnimation(WorkerAction.DRAW_WATER_2, cbobRomBobsLst, CbobRomBobsLst.DRAWING_WATER_2);
        helperCollector.addAnimation(WorkerAction.DRAW_WATER_3, cbobRomBobsLst, CbobRomBobsLst.DRAWING_WATER_3);

        fishermanCollector.readCargoImagesFromBob(FISH, FISHERMAN_BOB.getBodyType(), FISHERMAN_WITH_FISH_CARGO_BOB_ID, bob);

        // Lower fishing rod
        fishermanCollector.addWorkAnimationInDirection(LOWER_FISHING_ROD, EAST, cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_EAST);
        fishermanCollector.addWorkAnimationInDirection(LOWER_FISHING_ROD, SOUTH_EAST, cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_SOUTH_EAST);
        fishermanCollector.addWorkAnimationInDirection(LOWER_FISHING_ROD, SOUTH_WEST, cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_SOUTH_WEST);
        fishermanCollector.addWorkAnimationInDirection(LOWER_FISHING_ROD, WEST, cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_WEST);
        fishermanCollector.addWorkAnimationInDirection(LOWER_FISHING_ROD, WEST, cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_NORTH_WEST);
        fishermanCollector.addWorkAnimationInDirection(LOWER_FISHING_ROD, WEST, cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_NORTH_EAST);

        // Keep fishing
        fishermanCollector.addWorkAnimationInDirection(FISHING, EAST, cbobRomBobsLst, CbobRomBobsLst.FISHING_EAST);
        fishermanCollector.addWorkAnimationInDirection(FISHING, SOUTH_EAST, cbobRomBobsLst, CbobRomBobsLst.FISHING_SOUTH_EAST);
        fishermanCollector.addWorkAnimationInDirection(FISHING, SOUTH_WEST, cbobRomBobsLst, CbobRomBobsLst.FISHING_SOUTH_WEST);
        fishermanCollector.addWorkAnimationInDirection(FISHING, WEST, cbobRomBobsLst, CbobRomBobsLst.FISHING_WEST);
        fishermanCollector.addWorkAnimationInDirection(FISHING, WEST, cbobRomBobsLst, CbobRomBobsLst.FISHING_NORTH_WEST);
        fishermanCollector.addWorkAnimationInDirection(FISHING, WEST, cbobRomBobsLst, CbobRomBobsLst.FISHING_NORTH_EAST);

        // Pull up fish
        fishermanCollector.addWorkAnimationInDirection(PULL_UP_FISHING_ROD, EAST, cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_EAST);
        fishermanCollector.addWorkAnimationInDirection(PULL_UP_FISHING_ROD, SOUTH_EAST, cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_SOUTH_EAST);
        fishermanCollector.addWorkAnimationInDirection(PULL_UP_FISHING_ROD, SOUTH_WEST, cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_SOUTH_WEST);
        fishermanCollector.addWorkAnimationInDirection(PULL_UP_FISHING_ROD, WEST, cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_WEST);
        fishermanCollector.addWorkAnimationInDirection(PULL_UP_FISHING_ROD, WEST, cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_NORTH_WEST);
        fishermanCollector.addWorkAnimationInDirection(PULL_UP_FISHING_ROD, WEST, cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_NORTH_EAST);

        farmerCollector.readCargoImagesFromBob(WHEAT, FARMER_BOB.getBodyType(), FARMER_WITH_WHEAT_CARGO_BOB_ID, bob);

        farmerCollector.addAnimation(PLANTING_WHEAT, cbobRomBobsLst, CbobRomBobsLst.SOWING);
        farmerCollector.addAnimation(HARVESTING, cbobRomBobsLst, CbobRomBobsLst.HARVESTING);

        pigBreederCollector.readCargoImagesFromBob(PIG, PIG_BREEDER_BOB.getBodyType(), PIG_BREEDER_WITH_PIG_CARGO_BOB_ID, bob);

        millerCollector.readCargoImagesFromBob(FLOUR, MILLER_BOB.getBodyType(), MILLER_WITH_FLOUR_CARGO_BOB_ID, bob);

        bakerCollector.readCargoImagesFromBob(BREAD, BAKER_BOB.getBodyType(), BAKER_WITH_BREAD_CARGO_BOB_ID, bob);

        bakerCollector.addAnimation(OPEN_OVEN, cbobRomBobsLst, CbobRomBobsLst.BAKING);

        // TODO: Handle brewer and/or well worker

        brewerCollector.addAnimation(DRINKING_BEER, cbobRomBobsLst, CbobRomBobsLst.DRINKING_BEER);

        // TODO: Handle metalworker carrying "shift gear". Assume it's tongs

        metalWorkerCollector.readCargoImagesFromBob(TONGS, METAL_WORKER_BOB.getBodyType(), METAL_WORKER_WITH_TONGS_CARGO_BOB_ID, bob);
        metalWorkerCollector.readCargoImagesFromBob(HAMMER, METAL_WORKER_BOB.getBodyType(), METAL_WORKER_WITH_HAMMER_CARGO_BOB_ID, bob);
        metalWorkerCollector.readCargoImagesFromBob(AXE, METAL_WORKER_BOB.getBodyType(), METAL_WORKER_WITH_AXE_CARGO_BOB_ID, bob);
        metalWorkerCollector.readCargoImagesFromBob(PICK_AXE, METAL_WORKER_BOB.getBodyType(), METAL_WORKER_WITH_PICK_AXE_CARGO_BOB_ID, bob);
        metalWorkerCollector.readCargoImagesFromBob(SHOVEL, METAL_WORKER_BOB.getBodyType(), METAL_WORKER_WITH_SHOVEL_CARGO_BOB_ID, bob);
        metalWorkerCollector.readCargoImagesFromBob(CRUCIBLE, METAL_WORKER_BOB.getBodyType(), METAL_WORKER_WITH_CRUCIBLE_CARGO_BOB_ID, bob);
        metalWorkerCollector.readCargoImagesFromBob(FISHING_ROD, METAL_WORKER_BOB.getBodyType(), METAL_WORKER_WITH_FISHING_ROD_CARGO_BOB_ID, bob);
        metalWorkerCollector.readCargoImagesFromBob(SCYTHE, METAL_WORKER_BOB.getBodyType(), METAL_WORKER_WITH_SCYTHE_CARGO_BOB_ID, bob);

        // TODO: bucket

        metalWorkerCollector.readCargoImagesFromBob(CLEAVER, METAL_WORKER_BOB.getBodyType(), METAL_WORKER_WITH_CLEAVER_CARGO_BOB_ID, bob);
        metalWorkerCollector.readCargoImagesFromBob(ROLLING_PIN, METAL_WORKER_BOB.getBodyType(), METAL_WORKER_WITH_ROLLING_PIN_CARGO_BOB_ID, bob);

        // TODO: Is 2330-2335 a saw or a bow?
        metalWorkerCollector.readCargoImagesFromBob(SAW, METAL_WORKER_BOB.getBodyType(), METAL_WORKER_WITH_SAW_CARGO_BOB_ID, bob);

        hunterCollector.readCargoImagesFromBob(MEAT, HUNTER_BOB.getBodyType(), HUNTER_WITH_MEAT_CARGO_BOB_ID, bob);

        hunterCollector.addAnimation(SHOOTING, cbobRomBobsLst, CbobRomBobsLst.HUNTING);
        hunterCollector.addAnimation(PICKING_UP_MEAT, cbobRomBobsLst, CbobRomBobsLst.PICKING_UP_MEAT);

        shipwrightCollector.readCargoImagesFromBob(BOAT, SHIPWRIGHT_BOB.getBodyType(), SHIPWRIGHT_WITH_BOAT_CARGO_BOB_ID, bob);
        shipwrightCollector.readCargoImagesFromBob(PLANK, SHIPWRIGHT_BOB.getBodyType(), SHIPWRIGHT_WITH_PLANK_CARGO_BOB_ID, bob);

        // Write each worker image collection to file
        for (var workerImageCollection : workerCollectors.values()) {
            workerImageCollection.writeImageAtlas(toDir + "/", defaultPalette);
        }

        // Extract couriers
        var jobsBob = jobsBobResource.getBob();
        var carrierBob = BobDecoder.loadBobFile(fromDir + "/" + CarrierBob.FILENAME, defaultPalette);

        var thinCarrier = new WorkerImageCollection("thin-carrier-no-cargo");
        var fatCarrier = new WorkerImageCollection("fat-carrier-no-cargo");
        var thinCarrierWithCargo = new WorkerImageCollection("thin-carrier-with-cargo");
        var fatCarrierWithCargo = new WorkerImageCollection("fat-carrier-with-cargo");

        // Read body images
        thinCarrier.readBodyImagesFromBob(THIN, jobsBob);
        fatCarrier.readBodyImagesFromBob(FAT, jobsBob);
        thinCarrierWithCargo.readBodyImagesFromBob(THIN, carrierBob);
        fatCarrierWithCargo.readBodyImagesFromBob(FAT, carrierBob);

        // Read walking images without cargo
        thinCarrier.readHeadImagesWithoutCargoFromBob(THIN, HELPER_BOB_ID, jobsBob);
        fatCarrier.readHeadImagesWithoutCargoFromBob(FAT, HELPER_BOB_ID, jobsBob);

        thinCarrier.mergeBodyAndHeadImages(defaultPalette);
        fatCarrier.mergeBodyAndHeadImages(defaultPalette);

        // Read walking animation for each type of cargo
        for (var carrierCargo : CarrierCargo.values()) {
            var material = CarrierBob.CARGO_BOB_ID_TO_MATERIAL_MAP.get(carrierCargo.ordinal());

            if (material == null) {
                continue;
            }

            thinCarrierWithCargo.readCargoImagesFromBob(material, THIN, carrierCargo.ordinal(), carrierBob);
            fatCarrierWithCargo.readCargoImagesFromBob(material, FAT, carrierCargo.ordinal(), carrierBob);
        }

        thinCarrier.addShadowImages(EAST, map0ZLst, Map0ZLst.WALKING_EAST_SHADOW);
        thinCarrier.addShadowImages(SOUTH_EAST, map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW);
        thinCarrier.addShadowImages(SOUTH_WEST, map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW);
        thinCarrier.addShadowImages(WEST, map0ZLst, Map0ZLst.WALKING_WEST_SHADOW);
        thinCarrier.addShadowImages(NORTH_WEST, map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW);
        thinCarrier.addShadowImages(NORTH_EAST, map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW);

        fatCarrier.addShadowImages(EAST, map0ZLst, Map0ZLst.WALKING_EAST_SHADOW);
        fatCarrier.addShadowImages(SOUTH_EAST, map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW);
        fatCarrier.addShadowImages(SOUTH_WEST, map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW);
        fatCarrier.addShadowImages(WEST, map0ZLst, Map0ZLst.WALKING_WEST_SHADOW);
        fatCarrier.addShadowImages(NORTH_WEST, map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW);
        fatCarrier.addShadowImages(NORTH_EAST, map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW);

        thinCarrierWithCargo.addShadowImages(EAST, map0ZLst, Map0ZLst.WALKING_EAST_SHADOW);
        thinCarrierWithCargo.addShadowImages(SOUTH_EAST, map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW);
        thinCarrierWithCargo.addShadowImages(SOUTH_WEST, map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW);
        thinCarrierWithCargo.addShadowImages(WEST, map0ZLst, Map0ZLst.WALKING_WEST_SHADOW);
        thinCarrierWithCargo.addShadowImages(NORTH_WEST, map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW);
        thinCarrierWithCargo.addShadowImages(NORTH_EAST, map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW);

        fatCarrierWithCargo.addShadowImages(EAST, map0ZLst, Map0ZLst.WALKING_EAST_SHADOW);
        fatCarrierWithCargo.addShadowImages(SOUTH_EAST, map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW);
        fatCarrierWithCargo.addShadowImages(SOUTH_WEST, map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW);
        fatCarrierWithCargo.addShadowImages(WEST, map0ZLst, Map0ZLst.WALKING_WEST_SHADOW);
        fatCarrierWithCargo.addShadowImages(NORTH_WEST, map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW);
        fatCarrierWithCargo.addShadowImages(NORTH_EAST, map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW);

        // Add animations for when the couriers are bored
        fatCarrier.addAnimation(CHEW_GUM, cbobRomBobsLst, CbobRomBobsLst.CHEW_GUM);
        fatCarrier.addAnimation(SIT_DOWN, cbobRomBobsLst, CbobRomBobsLst.SIT_DOWN);
        thinCarrier.addAnimation(READ_NEWSPAPER, cbobRomBobsLst, CbobRomBobsLst.READ_NEWSPAPER);
        thinCarrier.addAnimation(TOUCH_NOSE, cbobRomBobsLst, CbobRomBobsLst.TOUCH_NOSE);
        thinCarrier.addAnimation(JUMP_SKIP_ROPE, cbobRomBobsLst, CbobRomBobsLst.JUMP_SKIP_ROPE);

        // Write the image atlases to files
        thinCarrier.writeImageAtlas(toDir + "/", defaultPalette);
        fatCarrier.writeImageAtlas(toDir + "/", defaultPalette);
        thinCarrierWithCargo.writeImageAtlas(toDir + "/", defaultPalette);
        fatCarrierWithCargo.writeImageAtlas(toDir + "/", defaultPalette);
    }

    static Bitmap mergeBodyAndHead(PlayerBitmap body, PlayerBitmap head, PlayerColor playerColor, boolean hasPlayerColor, Point maxOrigin, Palette defaultPalette) {
        var merged = new Bitmap(
                maxOrigin.x + maxOrigin.x,
                maxOrigin.y + maxOrigin.y,
                maxOrigin.x,
                maxOrigin.y,
                defaultPalette,
                TextureFormat.BGRA);

        // Draw the body
        var bodyVisibleArea = body.getVisibleArea();
        var bodyToUpperLeft = new Point(maxOrigin.x - body.getOffsetsForVisibleImage().x, maxOrigin.y - body.getOffsetsForVisibleImage().y);
        var bodyFromUpperLeft = bodyVisibleArea.getUpperLeftCoordinate();

        if (hasPlayerColor) {
            merged.copyNonTransparentPixels(
                    body.getBitmapForPlayer(playerColor),
                    bodyToUpperLeft,
                    bodyFromUpperLeft,
                    bodyVisibleArea.getDimension());
        } else {
            merged.copyNonTransparentPixels(
                    body,
                    bodyToUpperLeft,
                    bodyFromUpperLeft,
                    bodyVisibleArea.getDimension());
        }

        // Draw the head
        var headVisibleArea = head.getVisibleArea();

        var headToUpperLeft = new Point(maxOrigin.x - head.getOffsetsForVisibleImage().x, maxOrigin.y - head.getOffsetsForVisibleImage().y);
        var headFromUpperLeft = headVisibleArea.getUpperLeftCoordinate();

        merged.copyNonTransparentPixels(head, headToUpperLeft, headFromUpperLeft, headVisibleArea.getDimension());

        return merged;
    }
}
