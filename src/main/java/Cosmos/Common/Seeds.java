package Cosmos.Common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Seeds {

    public static ArrayList<String> getSeeds() {
        File directory = new File(System.getProperty("user.dir"));
        assert(directory.isDirectory());

        File content = null;
        while(true) {
            content = new File(directory + "/Seeds.txt");
            if (content.exists()) {
                break;
            } else {
                directory = directory.getParentFile();
            }
        }

        try {
            return new ArrayList<String>(List.of(Files.readString(content.toPath(), Charset.defaultCharset()).split("\n")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
