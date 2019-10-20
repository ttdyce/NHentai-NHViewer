package com.github.ttdyce.nhviewer;

import com.github.ttdyce.nhviewer.model.comic.Comic;
import com.google.gson.Gson;

import org.junit.Test;

public class ComicUnitTest {
    @Test
    public void comic_creation_by_gson() {
        Gson gson = new Gson();
        Comic comic = gson.fromJson(sampleComicJSON, Comic.class);
    }

    private String sampleComicJSON = "{\n" +
            "  \"id\": 284928,\n" +
            "  \"media_id\": \"1483523\",\n" +
            "  \"title\": {\n" +
            "    \"english\": \"(C96) [ParadiseGom (Gorgonzola)] Asedaku. (Captain Earth) [Chinese] [黑肉哈娜騷度破表GGININ大爆射地球隊長沒看過的人值得一補漢化]\",\n" +
            "    \"japanese\": \"(C96) [ParadiseGom (ごるごんぞーら)] 汗だく。 (キャプテン・アース) [中国翻訳]\",\n" +
            "    \"pretty\": \"Asedaku.\"\n" +
            "  },\n" +
            "  \"images\": {\n" +
            "    \"pages\": [\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1805\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1791\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1842\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1791\n" +
            "      },\n" +
            "      {\n" +
            "        \"t\": \"j\",\n" +
            "        \"w\": 1280,\n" +
            "        \"h\": 1791\n" +
            "      }\n" +
            "    ],\n" +
            "    \"cover\": {\n" +
            "      \"t\": \"j\",\n" +
            "      \"w\": 350,\n" +
            "      \"h\": 494\n" +
            "    },\n" +
            "    \"thumbnail\": {\n" +
            "      \"t\": \"j\",\n" +
            "      \"w\": 250,\n" +
            "      \"h\": 353\n" +
            "    }\n" +
            "  },\n" +
            "  \"scanlator\": \"\",\n" +
            "  \"upload_date\": 1568457177,\n" +
            "  \"tags\": [\n" +
            "    {\n" +
            "      \"id\": 8454,\n" +
            "      \"type\": \"artist\",\n" +
            "      \"name\": \"gorgonzola\",\n" +
            "      \"url\": \"/artist/gorgonzola/\",\n" +
            "      \"count\": 47\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 9360,\n" +
            "      \"type\": \"character\",\n" +
            "      \"name\": \"hana mutou\",\n" +
            "      \"url\": \"/character/hana-mutou/\",\n" +
            "      \"count\": 10\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 14900,\n" +
            "      \"type\": \"parody\",\n" +
            "      \"name\": \"captain earth\",\n" +
            "      \"url\": \"/parody/captain-earth/\",\n" +
            "      \"count\": 10\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 17249,\n" +
            "      \"type\": \"language\",\n" +
            "      \"name\": \"translated\",\n" +
            "      \"url\": \"/language/translated/\",\n" +
            "      \"count\": 96550\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 19018,\n" +
            "      \"type\": \"tag\",\n" +
            "      \"name\": \"dark skin\",\n" +
            "      \"url\": \"/tag/dark-skin/\",\n" +
            "      \"count\": 18354\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 19810,\n" +
            "      \"type\": \"group\",\n" +
            "      \"name\": \"paradisegom\",\n" +
            "      \"url\": \"/group/paradisegom/\",\n" +
            "      \"count\": 44\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 29963,\n" +
            "      \"type\": \"language\",\n" +
            "      \"name\": \"chinese\",\n" +
            "      \"url\": \"/language/chinese/\",\n" +
            "      \"count\": 36731\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 33172,\n" +
            "      \"type\": \"category\",\n" +
            "      \"name\": \"doujinshi\",\n" +
            "      \"url\": \"/category/doujinshi/\",\n" +
            "      \"count\": 208911\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 35762,\n" +
            "      \"type\": \"tag\",\n" +
            "      \"name\": \"sole female\",\n" +
            "      \"url\": \"/tag/sole-female/\",\n" +
            "      \"count\": 55235\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 35763,\n" +
            "      \"type\": \"tag\",\n" +
            "      \"name\": \"sole male\",\n" +
            "      \"url\": \"/tag/sole-male/\",\n" +
            "      \"count\": 49839\n" +
            "    }\n" +
            "  ],\n" +
            "  \"num_pages\": 36,\n" +
            "  \"num_favorites\": 0\n" +
            "}\n";
}
