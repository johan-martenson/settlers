package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.Area;
import org.appland.settlers.assets.BobResource;
import org.appland.settlers.assets.CarrierCargo;
import org.appland.settlers.assets.CompassDirection;
import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.JobType;
import org.appland.settlers.assets.Nation;
import org.appland.settlers.assets.RenderedWorker;
import org.appland.settlers.assets.StackedBitmaps;
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
import org.appland.settlers.assets.resources.Bob;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.resources.PlayerBitmap;
import org.appland.settlers.model.Material;
import org.appland.settlers.model.PlayerColor;
import org.appland.settlers.model.WorkerAction;

import java.awt.Point;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static org.appland.settlers.assets.CompassDirection.*;
import static org.appland.settlers.assets.Nation.*;
import static org.appland.settlers.assets.Utils.getImagesAt;
import static org.appland.settlers.assets.Utils.getPlayerImagesAt;
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

        /* Load worker image parts */
        var jobsBobList = LstDecoder.loadLstFile(fromDir + "/" + JobsBob.FILENAME, defaultPalette);
        var map0ZLst = LstDecoder.loadLstFile(fromDir + "/" + Map0ZLst.FILENAME, defaultPalette);
        var cbobRomBobsLst = LstDecoder.loadLstFile(fromDir + "/" + CbobRomBobsLst.FILENAME, defaultPalette);

        if (jobsBobList.size() != 1) {
            throw new RuntimeException("Wrong size of game resources in bob file. Must be 1, but was: " + jobsBobList.size());
        }

        if (! (jobsBobList.getFirst() instanceof BobResource jobsBobResource)) {
            throw new RuntimeException("Element must be Bob game resource. Was: " + jobsBobList.getFirst().getClass().getName());
        }

        /* Construct the worker details map */
        Map<JobType, WorkerDetails> workerDetailsMap = new EnumMap<>(JobType.class);

        // FIXME: assume RANGER == FORESTER

        /*
         * Translate ids:
         *  - 0 (Africans) -> 3
         *  - 1 (Japanese) -> 2
         *  - 2 (Romans)   -> 0
         *  - 3 (Vikings)  -> 1
         * */

        workerDetailsMap.put(JobType.HELPER, new WorkerDetails(false, JobsBob.HELPER_BOB_ID));
        workerDetailsMap.put(JobType.WOODCUTTER, JobsBob.WOODCUTTER_BOB);
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
        workerDetailsMap.put(JobType.CHAR_BURNER, new WorkerDetails(false, JobsBob.CHAR_BURNER_BOB_ID));

        /* Composite the worker images and animations */
        Map<JobType, RenderedWorker> renderedWorkers = BobDecoder.renderWorkerImages(jobsBobResource.getBob(), workerDetailsMap);
        Map<JobType, WorkerImageCollection> workerImageCollectors = new EnumMap<>(JobType.class);

        for (JobType jobType : JobType.values()) {
            RenderedWorker renderedWorker = renderedWorkers.get(jobType);

            WorkerImageCollection workerImageCollection = new WorkerImageCollection(jobType.name().toLowerCase());

            for (Nation nation : Nation.values()) {
                for (CompassDirection direction : CompassDirection.values()) {
                    StackedBitmaps[] stackedBitmaps = renderedWorker.getAnimation(nation, direction);

                    for (StackedBitmaps frame : stackedBitmaps) {
                        PlayerBitmap body = frame.getBitmaps().getFirst();
                        PlayerBitmap head = frame.getBitmaps().get(1);

                        /* Calculate the dimension */
                        Point maxOrigin = new Point(0, 0);

                        if (!frame.getBitmaps().isEmpty()) {
                            maxOrigin.x = Integer.MIN_VALUE;
                            maxOrigin.y = Integer.MIN_VALUE;

                            boolean hasPlayerColor = false;

                            for (Bitmap bitmap : frame.getBitmaps()) {
                                if (bitmap instanceof PlayerBitmap) {
                                    hasPlayerColor = true;
                                }

                                Area bitmapVisibleArea = bitmap.getVisibleArea();
                                Point bitmapOrigin = bitmap.getOrigin();

                                maxOrigin.x = Math.max(maxOrigin.x, bitmapOrigin.x);
                                maxOrigin.y = Math.max(maxOrigin.y, bitmapOrigin.y);

                                maxOrigin.x = Math.max(maxOrigin.x, bitmapVisibleArea.width() - bitmapOrigin.x);
                                maxOrigin.y = Math.max(maxOrigin.y, bitmapVisibleArea.height() - bitmapOrigin.y);
                            }

                            for (var playerColor : PlayerColor.values()) {

                                /* Create a bitmap to merge both body and head into */
                                Bitmap merged = new Bitmap(
                                        maxOrigin.x + maxOrigin.x,
                                        maxOrigin.y + maxOrigin.y,
                                        maxOrigin.x,
                                        maxOrigin.y,
                                        defaultPalette,
                                        TextureFormat.BGRA);

                                /* Draw the body */
                                Area bodyVisibleArea = body.getVisibleArea();
                                Point bodyToUpperLeft = new Point(maxOrigin.x - body.getOrigin().x, maxOrigin.y - body.getOrigin().y);
                                Point bodyFromUpperLeft = bodyVisibleArea.getUpperLeftCoordinate();

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

                                /* Draw the head */
                                Area headVisibleArea = head.getVisibleArea();

                                Point headToUpperLeft = new Point(maxOrigin.x - head.getOrigin().x, maxOrigin.y - head.getOrigin().y);
                                Point headFromUpperLeft = headVisibleArea.getUpperLeftCoordinate();

                                merged.copyNonTransparentPixels(head, headToUpperLeft, headFromUpperLeft, headVisibleArea.getDimension());

                                /* Store the image in the worker image collection */
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

            workerImageCollection.addShadowImages(EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(SOUTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(SOUTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(NORTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
            workerImageCollection.addShadowImages(NORTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

            // Store the worker image collector
            workerImageCollectors.put(jobType, workerImageCollection);
        }

        // Add cargo carrying images and animations
        WorkerImageCollection woodcutterImageCollector = workerImageCollectors.get(JobType.WOODCUTTER);
        WorkerImageCollection carpenterImageCollector = workerImageCollectors.get(JobType.CARPENTER);
        WorkerImageCollection fishermanImageCollector = workerImageCollectors.get(JobType.FISHER);
        WorkerImageCollection stonemasonImageCollector = workerImageCollectors.get(JobType.STONEMASON);
        WorkerImageCollection minterImageCollector = workerImageCollectors.get(JobType.MINTER);
        WorkerImageCollection minerImageCollector = workerImageCollectors.get(JobType.MINER);
        WorkerImageCollection farmerImageCollector = workerImageCollectors.get(JobType.FARMER);
        WorkerImageCollection pigBreederImageCollector = workerImageCollectors.get(JobType.PIG_BREEDER);
        WorkerImageCollection millerImageCollector = workerImageCollectors.get(JobType.MILLER);
        WorkerImageCollection bakerImageCollector = workerImageCollectors.get(JobType.BAKER);
        WorkerImageCollection metalWorkerImageCollector = workerImageCollectors.get(JobType.METALWORKER);
        WorkerImageCollection hunterWorkerImageCollector = workerImageCollectors.get(JobType.HUNTER);
        WorkerImageCollection shipwrightWorkerImageCollector = workerImageCollectors.get(JobType.SHIP_WRIGHT);
        WorkerImageCollection brewerWorkerImageCollector = workerImageCollectors.get(JobType.BREWER);
        WorkerImageCollection foresterWorkerImageCollector = workerImageCollectors.get(JobType.FORESTER);
        WorkerImageCollection planerWorkerImageCollector = workerImageCollectors.get(JobType.PLANER);
        WorkerImageCollection geologistWorkerImageCollector = workerImageCollectors.get(JobType.GEOLOGIST);
        WorkerImageCollection builderWorkerImageCollector = workerImageCollectors.get(JobType.BUILDER);
        WorkerImageCollection privateWorkerImageCollector = workerImageCollectors.get(JobType.PRIVATE);
        WorkerImageCollection privateFirstClassWorkerImageCollector = workerImageCollectors.get(JobType.PRIVATE_FIRST_CLASS);
        WorkerImageCollection sergeantWorkerImageCollector = workerImageCollectors.get(JobType.SERGEANT);
        WorkerImageCollection officerWorkerImageCollector = workerImageCollectors.get(JobType.OFFICER);
        WorkerImageCollection generalWorkerImageCollector = workerImageCollectors.get(JobType.GENERAL);

        Bob bob = jobsBobResource.getBob();

        woodcutterImageCollector.readCargoImagesFromBob(
                WOOD,
                JobsBob.WOODCUTTER_BOB.getBodyType(),
                JobsBob.WOODCUTTER_WITH_WOOD_CARGO_BOB_ID,
                bob
        );

        woodcutterImageCollector.addAnimation(WorkerAction.CUTTING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.CUTTING, 8));

        // Add roman military attacking
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_ATTACKING_EAST, 8));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_ATTACKING_WEST, 8));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_ATTACKING_EAST, 8));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_ATTACKING_WEST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_ATTACKING_EAST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_ATTACKING_WEST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_ATTACKING_EAST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_ATTACKING_WEST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_ATTACKING_EAST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_ATTACKING_WEST, 8));

        // Add roman military getting hit
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_SHIELD_UP_EAST, 8));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_SHIELD_UP_WEST, 6));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_STAND_ASIDE_EAST, 6));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_STAND_ASIDE_WEST, 7));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, SHIELD_UP, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_JUMP_BACK_EAST, 7));
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, SHIELD_UP, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_JUMP_BACK_WEST, 7));

        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_AVOIDING_HIT_EAST, 8));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_AVOIDING_HIT_WEST, 8));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_FLINCH_HIT_EAST, 6));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_FLINCH_HIT_WEST, 6));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_GETTING_HIT_EAST, 7));
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_PRIVATE_FIRST_CLASS_GETTING_HIT_WEST, 7));

        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_AVOIDING_HIT_EAST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_AVOIDING_HIT_WEST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_FLINCH_HIT_EAST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_FLINCH_HIT_WEST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_GETTING_HIT_EAST, 8));
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_SERGEANT_GETTING_HIT_WEST, 8));

        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_AVOIDING_HIT_EAST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_AVOIDING_HIT_WEST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_FLINCH_HIT_EAST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_FLINCH_HIT_WEST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_GETTING_HIT_EAST, 8));
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_OFFICER_GETTING_HIT_WEST, 7));

        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_AVOIDING_HIT_EAST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, JUMP_BACK, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_AVOIDING_HIT_WEST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_FLINCH_HIT_EAST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, STAND_ASIDE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_FLINCH_HIT_WEST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, EAST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_GETTING_HIT_EAST, 8));
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(ROMANS, WEST, GET_HIT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.ROMAN_GENERAL_GETTING_HIT_WEST, 8));

        privateWorkerImageCollector.addAnimation(DIE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING, 12));
        privateFirstClassWorkerImageCollector.addAnimation(DIE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING, 12));
        sergeantWorkerImageCollector.addAnimation(DIE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING, 12));
        officerWorkerImageCollector.addAnimation(DIE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING, 12));
        generalWorkerImageCollector.addAnimation(DIE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOLDIER_DYING, 12));

        // Add Japanese soldiers attacking
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_EAST_ATTACKING);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_WEST_ATTACKING);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_EAST_ATTACKING);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_WEST_ATTACKING);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_EAST_ATTACKING);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_WEST_ATTACKING);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_EAST_ATTACKING);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_WEST_ATTACKING);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_EAST_ATTACKING);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_WEST_ATTACKING);

        // Add other actions for Japanese soldiers
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_EAST_JUMPING_BACK);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_WEST_JUMPING_BACK);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_EAST_STAND_ASIDE);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_WEST_STAND_ASIDE);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_EAST_SHIELD_UP);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_WEST_SHIELD_UP);

        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_EAST_JUMPING_BACK);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_WEST_JUMPING_BACK);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_EAST_STAND_ASIDE);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_WEST_STAND_ASIDE);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_EAST_SHIELD_UP);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_PRIVATE_FIRST_CLASS_WEST_SHIELD_UP);

        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_EAST_JUMPING_BACK);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_WEST_JUMPING_BACK);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_EAST_STAND_ASIDE);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_WEST_STAND_ASIDE);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_EAST_SHIELD_UP);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_SERGEANT_WEST_SHIELD_UP);

        officerWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_EAST_JUMPING_BACK);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_WEST_JUMPING_BACK);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_EAST_STAND_ASIDE);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_WEST_STAND_ASIDE);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_EAST_SHIELD_UP);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_OFFICER_WEST_SHIELD_UP);

        generalWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_EAST_JUMPING_BACK);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_WEST_JUMPING_BACK);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_EAST_STAND_ASIDE);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_WEST_STAND_ASIDE);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_EAST_SHIELD_UP);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(JAPANESE, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.JAPANESE_GENERAL_WEST_SHIELD_UP);


        // Add Viking soldiers attacking
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_EAST_ATTACKING);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_WEST_ATTACKING);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_EAST_ATTACKING);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_WEST_ATTACKING);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_EAST_ATTACKING);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_WEST_ATTACKING);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_EAST_ATTACKING);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_WEST_ATTACKING);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_EAST_ATTACKING);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_WEST_ATTACKING);

        // Add other actions for Viking soldiers
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_EAST_JUMPING_BACK);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_WEST_JUMPING_BACK);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_EAST_STAND_ASIDE);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_WEST_STAND_ASIDE);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_EAST_SHIELD_UP);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_WEST_SHIELD_UP);

        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_EAST_JUMPING_BACK);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_WEST_JUMPING_BACK);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_EAST_STAND_ASIDE);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_WEST_STAND_ASIDE);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_EAST_SHIELD_UP);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_PRIVATE_FIRST_CLASS_WEST_SHIELD_UP);

        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_EAST_JUMPING_BACK);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_WEST_JUMPING_BACK);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_EAST_STAND_ASIDE);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_WEST_STAND_ASIDE);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_EAST_SHIELD_UP);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_SERGEANT_WEST_SHIELD_UP);

        officerWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_EAST_JUMPING_BACK);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_WEST_JUMPING_BACK);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_EAST_STAND_ASIDE);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_WEST_STAND_ASIDE);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_EAST_SHIELD_UP);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_OFFICER_WEST_SHIELD_UP);

        generalWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_EAST_JUMPING_BACK);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_WEST_JUMPING_BACK);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_EAST_STAND_ASIDE);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_WEST_STAND_ASIDE);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_EAST_SHIELD_UP);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(VIKINGS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.VIKING_GENERAL_WEST_SHIELD_UP);


        // Add African soldiers attacking
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_EAST_ATTACKING);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_WEST_ATTACKING);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_EAST_ATTACKING);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_WEST_ATTACKING);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_EAST_ATTACKING);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_WEST_ATTACKING);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_EAST_ATTACKING);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_WEST_ATTACKING);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_EAST_ATTACKING);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, HIT, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_WEST_ATTACKING);

        // Add other actions for African soldiers
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_EAST_JUMPING_BACK);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_WEST_JUMPING_BACK);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_EAST_STAND_ASIDE);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_WEST_STAND_ASIDE);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_EAST_SHIELD_UP);
        privateWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_WEST_SHIELD_UP);

        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_EAST_JUMPING_BACK);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_WEST_JUMPING_BACK);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_EAST_STAND_ASIDE);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_WEST_STAND_ASIDE);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_EAST_SHIELD_UP);
        privateFirstClassWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_PRIVATE_FIRST_CLASS_WEST_SHIELD_UP);

        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_EAST_JUMPING_BACK);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_WEST_JUMPING_BACK);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_EAST_STAND_ASIDE);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_WEST_STAND_ASIDE);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_EAST_SHIELD_UP);
        sergeantWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_SERGEANT_WEST_SHIELD_UP);

        officerWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_EAST_JUMPING_BACK);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_WEST_JUMPING_BACK);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_EAST_STAND_ASIDE);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_WEST_STAND_ASIDE);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_EAST_SHIELD_UP);
        officerWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_OFFICER_WEST_SHIELD_UP);

        generalWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_EAST_JUMPING_BACK);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, JUMP_BACK, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_WEST_JUMPING_BACK);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_EAST_STAND_ASIDE);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, STAND_ASIDE, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_WEST_STAND_ASIDE);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, EAST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_EAST_SHIELD_UP);
        generalWorkerImageCollector.addNationSpecificAnimationInDirection(AFRICANS, WEST, SHIELD_UP, cbobRomBobsLst, CbobRomBobsLst.AFRICAN_GENERAL_WEST_SHIELD_UP);


        // Add regular worker animations
        carpenterImageCollector.readCargoImagesFromBob(
                PLANK,
                JobsBob.CARPENTER_BOB.getBodyType(),
                JobsBob.CARPENTER_WITH_PLANK_BOB_ID,
                bob
        );

        carpenterImageCollector.addAnimation(WorkerAction.SAWING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SAWING, 6));

        stonemasonImageCollector.readCargoImagesFromBob(
                STONE,
                JobsBob.STONEMASON_BOB.getBodyType(),
                JobsBob.STONEMASON_WITH_STONE_CARGO_BOB_ID,
                bob
        );

        stonemasonImageCollector.addAnimation(WorkerAction.HACKING_STONE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.HACKING_STONE, 8));

        foresterWorkerImageCollector.addAnimation(WorkerAction.PLANTING_TREE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.DIGGING_AND_PLANTING, 36));

        planerWorkerImageCollector.addAnimation(WorkerAction.DIGGING_AND_STOMPING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.DIGGING_AND_STOMPING, 26));

        geologistWorkerImageCollector.addAnimation(WorkerAction.INVESTIGATING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.INVESTIGATING, 16));

        builderWorkerImageCollector.addAnimation(WorkerAction.HAMMERING_HOUSE_HIGH_AND_LOW, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.HAMMERING_HOUSE_HIGH_AND_LOW, 8));
        builderWorkerImageCollector.addAnimation(WorkerAction.INSPECTING_HOUSE_CONSTRUCTION, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.INSPECTING_HOUSE_CONSTRUCTION, 4));

        minterImageCollector.readCargoImagesFromBob(
                COIN,
                JobsBob.MINTER_BOB.getBodyType(),
                JobsBob.MINTER_WITH_COIN_CARGO_BOB_ID,
                bob
        );

        // TODO: add work animation for minter

        minerImageCollector.readCargoImagesFromBob(
                GOLD,
                JobsBob.MINER_BOB.getBodyType(),
                JobsBob.MINER_WITH_GOLD_CARGO_BOB_ID,
                bob
        );

        minerImageCollector.readCargoImagesFromBob(
                IRON,
                JobsBob.MINER_BOB.getBodyType(),
                JobsBob.MINER_WITH_IRON_CARGO_BOB_ID,
                bob
        );

        minerImageCollector.readCargoImagesFromBob(
                COAL,
                JobsBob.MINER_BOB.getBodyType(),
                JobsBob.MINER_WITH_COAL_CARGO_BOB_ID,
                bob
        );

        minerImageCollector.readCargoImagesFromBob(
                STONE,
                JobsBob.MINER_BOB.getBodyType(),
                JobsBob.MINER_WITH_STONE_CARGO_BOB_ID,
                bob
        );

        // TODO: add work animation for miner

        // TODO: job id 69 == carrying crucible/anvil?

        fishermanImageCollector.readCargoImagesFromBob(
                FISH,
                JobsBob.FISHERMAN_BOB.getBodyType(),
                JobsBob.FISHERMAN_WITH_FISH_CARGO_BOB_ID,
                bob
        );

        // Lower fishing rod
        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                EAST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                SOUTH_EAST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_SOUTH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                SOUTH_WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_SOUTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_NORTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.LOWER_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.LOWERING_FISHING_ROD_NORTH_EAST, 8));

        // Keep fishing
        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                EAST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                SOUTH_EAST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_SOUTH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                SOUTH_WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_SOUTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_NORTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.FISHING,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.FISHING_NORTH_EAST, 8));

        // Pull up fish
        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                EAST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                SOUTH_EAST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_SOUTH_EAST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                SOUTH_WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_SOUTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_NORTH_WEST, 8));

        fishermanImageCollector.addWorkAnimationInDirection(
                WorkerAction.PULL_UP_FISHING_ROD,
                WEST,
                getImagesAt(cbobRomBobsLst, CbobRomBobsLst.PULL_UP_FISH_NORTH_EAST, 8));

        farmerImageCollector.readCargoImagesFromBob(
                WHEAT,
                JobsBob.FARMER_BOB.getBodyType(),
                JobsBob.FARMER_WITH_WHEAT_CARGO_BOB_ID,
                bob
        );

        farmerImageCollector.addAnimation(WorkerAction.PLANTING_WHEAT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SOWING, 8));
        farmerImageCollector.addAnimation(WorkerAction.HARVESTING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.HARVESTING, 8));

        pigBreederImageCollector.readCargoImagesFromBob(
                PIG,
                JobsBob.PIG_BREEDER_BOB.getBodyType(),
                JobsBob.PIG_BREEDER_WITH_PIG_CARGO_BOB_ID,
                bob
        );

        millerImageCollector.readCargoImagesFromBob(
                FLOUR,
                JobsBob.MILLER_BOB.getBodyType(),
                JobsBob.MILLER_WITH_FLOUR_CARGO_BOB_ID,
                bob
        );

        bakerImageCollector.readCargoImagesFromBob(
                BREAD,
                JobsBob.BAKER_BOB.getBodyType(),
                JobsBob.BAKER_WITH_BREAD_CARGO_BOB_ID,
                bob
        );

        bakerImageCollector.addAnimation(WorkerAction.BAKING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.BAKING, 8));

        // TODO: Handle brewer and/or well worker

        brewerWorkerImageCollector.addAnimation(WorkerAction.DRINKING_BEER, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.DRINKING_BEER, 8));

        // TODO: Handle metalworker carrying "shift gear". Assume it's tongs

        metalWorkerImageCollector.readCargoImagesFromBob(
                TONGS,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_TONGS_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                HAMMER,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_HAMMER_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                AXE,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_AXE_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                PICK_AXE,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_PICK_AXE_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                SHOVEL,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_SHOVEL_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                CRUCIBLE,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_CRUCIBLE_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                FISHING_ROD,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_FISHING_ROD_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                SCYTHE,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_SCYTHE_CARGO_BOB_ID,
                bob
        );

        // TODO: bucket

        metalWorkerImageCollector.readCargoImagesFromBob(
                CLEAVER,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_CLEAVER_CARGO_BOB_ID,
                bob
        );

        metalWorkerImageCollector.readCargoImagesFromBob(
                ROLLING_PIN,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_ROLLING_PIN_CARGO_BOB_ID,
                bob
        );

        // TODO: Is 2330-2335 a saw or a bow?
        metalWorkerImageCollector.readCargoImagesFromBob(
                SAW,
                JobsBob.METAL_WORKER_BOB.getBodyType(),
                JobsBob.METAL_WORKER_WITH_SAW_CARGO_BOB_ID,
                bob
        );

        hunterWorkerImageCollector.readCargoImagesFromBob(
                MEAT,
                JobsBob.HUNTER_BOB.getBodyType(),
                JobsBob.HUNTER_WITH_MEAT_CARGO_BOB_ID,
                bob
        );

        hunterWorkerImageCollector.addAnimation(WorkerAction.SHOOTING, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.HUNTING, 13));
        hunterWorkerImageCollector.addAnimation(WorkerAction.PICKING_UP_MEAT, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.PICKING_UP_MEAT, 12));

        shipwrightWorkerImageCollector.readCargoImagesFromBob(
                BOAT,
                JobsBob.SHIPWRIGHT_BOB.getBodyType(),
                JobsBob.SHIPWRIGHT_WITH_BOAT_CARGO_BOB_ID,
                bob
        );

        shipwrightWorkerImageCollector.readCargoImagesFromBob(
                PLANK,
                JobsBob.SHIPWRIGHT_BOB.getBodyType(),
                JobsBob.SHIPWRIGHT_WITH_PLANK_CARGO_BOB_ID,
                bob
        );

        // Write each worker image collection to file
        for (WorkerImageCollection workerImageCollection : workerImageCollectors.values()) {
            workerImageCollection.writeImageAtlas(toDir + "/", defaultPalette);
        }

        // Extract couriers
        Bob jobsBob = jobsBobResource.getBob();
        Bob carrierBob = BobDecoder.loadBobFile(fromDir + "/" + CarrierBob.FILENAME, defaultPalette);

        WorkerImageCollection thinCarrier = new WorkerImageCollection("thin-carrier-no-cargo");
        WorkerImageCollection fatCarrier = new WorkerImageCollection("fat-carrier-no-cargo");
        WorkerImageCollection thinCarrierWithCargo = new WorkerImageCollection("thin-carrier-with-cargo");
        WorkerImageCollection fatCarrierWithCargo = new WorkerImageCollection("fat-carrier-with-cargo");

        // Read body images
        thinCarrier.readBodyImagesFromBob(THIN, jobsBob);
        fatCarrier.readBodyImagesFromBob(FAT, jobsBob);
        thinCarrierWithCargo.readBodyImagesFromBob(THIN, carrierBob);
        fatCarrierWithCargo.readBodyImagesFromBob(FAT, carrierBob);

        // Read walking images without cargo
        thinCarrier.readHeadImagesWithoutCargoFromBob(THIN, JobsBob.HELPER_BOB_ID, jobsBob);
        fatCarrier.readHeadImagesWithoutCargoFromBob(FAT, JobsBob.HELPER_BOB_ID, jobsBob);

        thinCarrier.mergeBodyAndHeadImages(defaultPalette);
        fatCarrier.mergeBodyAndHeadImages(defaultPalette);

        // Read walking animation for each type of cargo
        for (CarrierCargo carrierCargo : CarrierCargo.values()) {
            Material material = CarrierBob.CARGO_BOB_ID_TO_MATERIAL_MAP.get(carrierCargo.ordinal());

            if (material == null) {
                continue;
            }

            thinCarrierWithCargo.readCargoImagesFromBob(material, THIN, carrierCargo.ordinal(), carrierBob);
            fatCarrierWithCargo.readCargoImagesFromBob(material, FAT, carrierCargo.ordinal(), carrierBob);
        }

        thinCarrier.addShadowImages(EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(SOUTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(SOUTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(NORTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrier.addShadowImages(NORTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        fatCarrier.addShadowImages(EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(SOUTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(SOUTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(NORTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrier.addShadowImages(NORTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        thinCarrierWithCargo.addShadowImages(EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(SOUTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(SOUTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(NORTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        thinCarrierWithCargo.addShadowImages(NORTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        fatCarrierWithCargo.addShadowImages(EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_EAST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(SOUTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_EAST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(SOUTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_SOUTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_WEST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(NORTH_WEST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_WEST_SHADOW_ANIMATION, 8));
        fatCarrierWithCargo.addShadowImages(NORTH_EAST, getImagesAt(map0ZLst, Map0ZLst.WALKING_NORTH_EAST_SHADOW_ANIMATION, 8));

        // Add animations for when the couriers are bored
        fatCarrier.addAnimation(CHEW_GUM, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.CHEW_GUM, 8));
        fatCarrier.addAnimation(SIT_DOWN, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.SIT_DOWN, 5));
        thinCarrier.addAnimation(READ_NEWSPAPER, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.READ_NEWSPAPER, 7));
        thinCarrier.addAnimation(TOUCH_NOSE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.TOUCH_NOSE, 5));
        thinCarrier.addAnimation(JUMP_SKIP_ROPE, getPlayerImagesAt(cbobRomBobsLst, CbobRomBobsLst.JUMP_SKIP_ROPE, 7));

        // Write the image atlases to files
        thinCarrier.writeImageAtlas(toDir + "/", defaultPalette);
        fatCarrier.writeImageAtlas(toDir + "/", defaultPalette);
        thinCarrierWithCargo.writeImageAtlas(toDir + "/", defaultPalette);
        fatCarrierWithCargo.writeImageAtlas(toDir + "/", defaultPalette);
    }
}
