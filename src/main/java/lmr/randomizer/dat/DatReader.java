package lmr.randomizer.dat;

import lmr.randomizer.DataFromFile;
import lmr.randomizer.Settings;
import lmr.randomizer.dat.conversation.CheckBlock;
import lmr.randomizer.dat.shop.BlockCmdSingle;
import lmr.randomizer.dat.shop.BlockStringData;
import lmr.randomizer.dat.shop.ShopBlock;
import lmr.randomizer.update.GameDataTracker;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thezerothcat on 7/26/2017.
 */
public final class DatReader {
    private DatReader() {
    }


    private static ShopBlock buildShopBlock(int blockIndex, DataInputStream dataInputStream, int numberOfShortsInThisBlock) throws IOException {
        int dataIndex = 0;
        ShopBlock shopBlock = new ShopBlock(blockIndex);

        List<BlockListData> shopBlockData = new ArrayList<>();
        for(int i = 0; i < 4; i ++) {
            short cmdShort = dataInputStream.readShort();
            ++dataIndex;

            short numberOfOpts = dataInputStream.readShort();
            ++dataIndex;
            BlockListData blockListData = new BlockListData(cmdShort, numberOfOpts);
            for(int blockListDataIndex = 0; blockListDataIndex < numberOfOpts; blockListDataIndex++) {
                blockListData.getData().add(dataInputStream.readShort());
                ++dataIndex;
            }

            dataInputStream.readShort(); // 0x000a
            ++dataIndex;

            shopBlockData.add(blockListData);
        }
        shopBlock.setInventoryItemArgsList(shopBlockData.get(0));
        shopBlock.setInventoryPriceList(shopBlockData.get(1));
        shopBlock.setInventoryCountList(shopBlockData.get(2));
        shopBlock.setFlagList(shopBlockData.get(3));

        for(int i = 0; i < 18; i ++) {
            BlockStringData blockStringData = new BlockStringData();
            dataIndex += populateBlockStringData(blockStringData, dataInputStream);
            shopBlock.setString(blockStringData, i);
        }

        dataInputStream.readShort(); // Ignore CMD
        ++dataIndex;
        dataInputStream.readShort(); // Ignore length, we already know it's 1
        ++dataIndex;
        shopBlock.setBackground(new BlockCmdSingle(dataInputStream.readShort()));
        ++dataIndex;
        dataInputStream.readShort(); // Ignore 0x000a
        ++dataIndex;

        dataInputStream.readShort(); // Ignore CMD
        ++dataIndex;
        dataInputStream.readShort(); // Ignore length, we already know it's 1
        ++dataIndex;
        shopBlock.setSprite(new BlockCmdSingle(dataInputStream.readShort()));
        ++dataIndex;
        dataInputStream.readShort(); // Ignore 0x000a
        ++dataIndex;

        // Exit flags
        short cmdShort = dataInputStream.readShort(); // Ignore CMD
        ++dataIndex;
        short numberOfOpts = dataInputStream.readShort();
        ++dataIndex;
        BlockListData blockListData = new BlockListData(cmdShort, numberOfOpts);
        for(int blockListDataIndex = 0; blockListDataIndex < numberOfOpts; blockListDataIndex++) {
            blockListData.getData().add(dataInputStream.readShort());
            ++dataIndex;
        }
        dataInputStream.readShort(); // Ignore 0x000a
        ++dataIndex;
        shopBlock.setExitFlagList(blockListData);

        dataInputStream.readShort(); // Ignore CMD
        ++dataIndex;
        dataInputStream.readShort(); // Ignore length, we already know it's 1
        ++dataIndex;
        shopBlock.setMusic(new BlockCmdSingle(dataInputStream.readShort()));
        ++dataIndex;
        dataInputStream.readShort(); // Ignore 0x000a
        ++dataIndex;

        BlockStringData blockStringData = new BlockStringData();
        dataIndex += populateBlockStringData(blockStringData, dataInputStream);
        shopBlock.setBunemonLocation(blockStringData);

        blockStringData = new BlockStringData();
        while(dataIndex < numberOfShortsInThisBlock) {
            blockStringData.getData().add(dataInputStream.readShort());
            ++dataIndex;
        }
        shopBlock.setBunemonText(blockStringData);
        return shopBlock;
    }

    private static Block buildCheckBlock(int blockIndex, DataInputStream dataInputStream, int numberOfShortsInThisBlock) throws IOException {
        int dataIndex = 0;
        CheckBlock checkBlock = new CheckBlock(blockIndex);

        while(dataIndex < numberOfShortsInThisBlock) {
            short cmdShort = dataInputStream.readShort();
            ++dataIndex;

            short numberOfOpts = dataInputStream.readShort();
            ++dataIndex;

            BlockListData blockListData = new BlockListData(cmdShort, numberOfOpts);
            for(int blockListDataIndex = 0; blockListDataIndex < numberOfOpts; blockListDataIndex++) {
                blockListData.getData().add(dataInputStream.readShort());
                ++dataIndex;
            }
            checkBlock.getFlagCheckReferences().add(blockListData);

            if(dataIndex < numberOfShortsInThisBlock) {
                dataInputStream.readShort(); // 0x000a
                ++dataIndex;
            }
        }
        return checkBlock;
    }

    private static int populateBlockStringData(BlockStringData blockStringData, DataInputStream dataInputStream) throws IOException{
        int dataIndex = 0;

        while (true) {
            short data = dataInputStream.readShort();
            ++dataIndex;


            if (data == 0x000a) {
//                s = unichr(o)
                return dataIndex;
            }
            else if(data == 0x000c || data == 0x0020) {
                blockStringData.getData().add(data);
            }
            else if (data >= 0x0040 && data <= 0x0050) {
                if (data == 0x0040) {
//                    cmd = "{FLAG %d:=%d}" % (ord(b[0]), ord(b[1]))
//                    b = b[2:]

                    blockStringData.getData().add(data);
                    blockStringData.getData().add(dataInputStream.readShort());
                    blockStringData.getData().add(dataInputStream.readShort());
                    ++dataIndex;
                    ++dataIndex;
                } else if (data == 0x0042) {
//                    cmd = "{ITEM %d}" % ord(b[0])
//                    b = b[1:]

                    blockStringData.getData().add(data);
                    blockStringData.getData().add(dataInputStream.readShort());
                    ++dataIndex;
                } else if (data == 0x0044) {
//                    cmd = "{CLS}"
//                    s = "<p>"
                    blockStringData.getData().add(data);
                } else if (data == 0x0045) {
//                    cmd = "{BR}"
//                    s = "<br>"
                    blockStringData.getData().add(data);
                } else if (data == 0x0046) {
//                    cmd = "{POSE %d}" % ord(b[0])
//                    b = b[1:]

                    blockStringData.getData().add(data);
                    blockStringData.getData().add(dataInputStream.readShort());
                    ++dataIndex;
                } else if (data == 0x0047) {
//                    cmd = "{MANTRA %d}" % ord(b[0])
//                    b = b[1:]

                    blockStringData.getData().add(data);
                    blockStringData.getData().add(dataInputStream.readShort());
                    ++dataIndex;
                } else if (data == 0x004a) {
//                    colors = [ord(x) for x in b[:3]]
//                    cmd = "{COL %03d-%03d-%03d}" % tuple(colors)
//                    if sum(colors) > 0:
//                        s = "<span style='color:#%02x%02x%02x'>" % tuple(200-x for x in colors)
//                    else:
//                        s = "</span>"
//                        b = b[3:] #TODO: colors not verified

                    if(blockStringData.getItemNameStartIndex() != null && blockStringData.getItemNameEndIndex() == null) {
                        blockStringData.setItemNameEndIndex(blockStringData.getData().size());
                    }

                    blockStringData.getData().add(data);
                    blockStringData.getData().add(dataInputStream.readShort());
                    blockStringData.getData().add(dataInputStream.readShort());
                    blockStringData.getData().add(dataInputStream.readShort());
                    ++dataIndex;
                    ++dataIndex;
                    ++dataIndex;

                    if(blockStringData.getItemNameStartIndex() == null) {
                        blockStringData.setItemNameStartIndex(blockStringData.getData().size());
                    }
                } else if (data == 0x004e) {
//                    lenopts = ord(b[0])
//                    opts = ["%d" % ord(x) for x in b[1:lenopts+1]]
//                    cmd = "{CMD %s}" % "-".join(opts)
//                    b = b[lenopts+1:]
                    blockStringData.getData().add(data);
                    short numberOfOpts = dataInputStream.readShort();
                    blockStringData.getData().add(numberOfOpts);
                    ++dataIndex;
                    for (int i = 0; i < numberOfOpts; i++) {
                        blockStringData.getData().add(dataInputStream.readShort());
                        ++dataIndex;
                    }
                } else if (data == 0x004f) {
//                    cmd = "{SCENE %d}" % ord(b[0])
//                    b = b[1:]
                    blockStringData.getData().add(data);
                    blockStringData.getData().add(dataInputStream.readShort());
                    ++dataIndex;
                } else {
//                    cmd = "{%02x}" % o
                    blockStringData.getData().add(data);
                }
            } else if (data >= 0x0100 && data <= 0x05c0) {
//                s = font00[o-0x0100]
                blockStringData.getData().add(data);
            } else if (data == 0x05c1) {
//                s = "Un"
                blockStringData.getData().add(data);
            } else if (data == 0x05c2) {
//                s = "defi"
                blockStringData.getData().add(data);
            } else if (data == 0x05c3) {
//                s = "ned"
                blockStringData.getData().add(data);
            } else {
//                s = "{%04x}" % o
                blockStringData.getData().add(data);
            }
        }
    }

    private static void addBlockContentsToBlock(Block block, DataInputStream dataInputStream, int numberOfShortsInThisBlock) throws IOException {
        int dataIndex = 0;
        while (dataIndex < numberOfShortsInThisBlock) {
            short data = dataInputStream.readShort();
            ++dataIndex;


            if(data == 0x000a || data == 0x000c || data == 0x0020) {
//                s = unichr(o)
                block.getBlockContents().add(new BlockSingleData(data));
            }
            else if(data >= 0x0040 && data <= 0x0050) {
                if (data == 0x0040) {
//                    cmd = "{FLAG %d:=%d}" % (ord(b[0]), ord(b[1]))
//                    b = b[2:]

                    block.getBlockContents().add(new BlockFlagData(data,
                            dataInputStream.readShort(), dataInputStream.readShort()));
                    ++dataIndex;
                    ++dataIndex;
                }
                else if (data == 0x0042) {
//                    cmd = "{ITEM %d}" % ord(b[0])
//                    b = b[1:]

                    block.getBlockContents().add(new BlockItemData(data, dataInputStream.readShort()));
                    ++dataIndex;
                }
                else if (data == 0x0044) {
//                    cmd = "{CLS}"
//                    s = "<p>"
                    block.getBlockContents().add(new BlockSingleData(data));
                }
                else if (data == 0x0045) {
//                    cmd = "{BR}"
//                    s = "<br>"
                    block.getBlockContents().add(new BlockSingleData(data));
                }
                else if (data == 0x0046) {
//                    cmd = "{POSE %d}" % ord(b[0])
//                    b = b[1:]

                    block.getBlockContents().add(new BlockPoseData(data, dataInputStream.readShort()));
                    ++dataIndex;
               }
                else if (data == 0x0047) {
//                    cmd = "{MANTRA %d}" % ord(b[0])
//                    b = b[1:]

                    block.getBlockContents().add(new BlockMantraData(data, dataInputStream.readShort()));
                    ++dataIndex;
                }
                else if (data == 0x004a) {
//                    colors = [ord(x) for x in b[:3]]
//                    cmd = "{COL %03d-%03d-%03d}" % tuple(colors)
//                    if sum(colors) > 0:
//                        s = "<span style='color:#%02x%02x%02x'>" % tuple(200-x for x in colors)
//                    else:
//                        s = "</span>"
//                        b = b[3:] #TODO: colors not verified

                    block.getBlockContents().add(new BlockColorsData(data, dataInputStream.readShort(),
                            dataInputStream.readShort(), dataInputStream.readShort()));
                    ++dataIndex;
                    ++dataIndex;
                    ++dataIndex;
                }
                else if (data == 0x004e) {
//                    lenopts = ord(b[0])
//                    opts = ["%d" % ord(x) for x in b[1:lenopts+1]]
//                    cmd = "{CMD %s}" % "-".join(opts)
//                    b = b[lenopts+1:]
                    short numberOfOpts = dataInputStream.readShort();
                    ++dataIndex;
                    BlockListData blockListData = new BlockListData(data, numberOfOpts);
                    for(int i = 0; i < numberOfOpts; i++) {
                        blockListData.getData().add(dataInputStream.readShort());
                        ++dataIndex;
                    }
                    block.getBlockContents().add(blockListData);
                }
                else if (data == 0x004f) {
//                    cmd = "{SCENE %d}" % ord(b[0])
//                    b = b[1:]
                    block.getBlockContents().add(new BlockSceneData(data, dataInputStream.readShort()));
                    ++dataIndex;
                }
                else {
//                    cmd = "{%02x}" % o
                    block.getBlockContents().add(new BlockSingleData(data));
                }
            }
            else if(data >= 0x0100 && data <= 0x05c0) {
//                s = font00[o-0x0100]
                block.getBlockContents().add(new BlockSingleData(data));
            }
            else if(data == 0x05c1) {
//                s = "Un"
                block.getBlockContents().add(new BlockSingleData(data));
            }
            else if(data == 0x05c2) {
//                s = "defi"
                block.getBlockContents().add(new BlockSingleData(data));
            }
            else if(data == 0x05c3) {
//                s = "ned"
                block.getBlockContents().add(new BlockSingleData(data));
            }
            else {
//                s = "{%04x}" % o
                block.getBlockContents().add(new BlockSingleData(data));
            }
        }
    }

    public static List<Block> getDatScriptInfo() throws Exception {
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(Settings.getBackupDatFile()));

        List<Block> datBlocks = new ArrayList<>();
        int numberOfBlocks = (int)dataInputStream.readShort();

        Block block;
        for(int blockIndex = 0; blockIndex < numberOfBlocks; blockIndex++) {
            int numberOfBytesInThisBlock = dataInputStream.readShort();

            if(DataFromFile.getMapOfShopNameToShopBlock().values().contains((Integer)blockIndex)) {
                block = buildShopBlock(blockIndex, dataInputStream, numberOfBytesInThisBlock / 2);
            }
            else if(blockIndex == 480 || blockIndex == 482 || blockIndex == 486) {
                block = buildCheckBlock(blockIndex, dataInputStream, numberOfBytesInThisBlock / 2);
            }
            else {
                block = new Block(blockIndex);
                addBlockContentsToBlock(block, dataInputStream, numberOfBytesInThisBlock / 2);
                if(blockIndex == 249) {
                    // Remove Mini Doll's becoming small flag from conversation.
                    Integer becomingSmallFlagIndex = null;
                    BlockContents blockContents;
                    for(int i = 0; i < block.getBlockContents().size(); i++) {
                        blockContents = block.getBlockContents().get(i);
                        if(blockContents instanceof BlockFlagData && ((BlockFlagData) blockContents).getWorldFlag() == 554) {
                            becomingSmallFlagIndex = i;
                        }
                    }
                    if(becomingSmallFlagIndex != null) {
                        block.getBlockContents().remove((int)becomingSmallFlagIndex);
                    }
                }
            }
            datBlocks.add(block);
            GameDataTracker.addBlock(block);
        }
        return datBlocks;
    }
}
