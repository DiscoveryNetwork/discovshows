package nl.parrotlync.discovshows.command;

import nl.parrotlync.discovshows.DiscovShows;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

abstract class TabArgument {
    protected String name;
    protected boolean personal = false;
    protected List<String> suggestions = new ArrayList<>();

    public List<String> getSuggestions() {
        return suggestions;
    }

    public List<String> getSuggestions(CommandSender sender) {
        return suggestions;
    }

    public String getName() {
        return name;
    }

    public boolean isPersonal() {
        return personal;
    }

    @Override
    public String toString() {
        return name;
    }
}

class StringTabArgument extends TabArgument {
    public StringTabArgument(String name, String[] suggestions) {
        this.name = name;
        this.suggestions.addAll(Arrays.asList(suggestions));
    }
}

class ShowTabArgument extends TabArgument {
    public ShowTabArgument() {
        name = "show";
    }

    @Override
    public List<String> getSuggestions() {
        suggestions.addAll(DiscovShows.getInstance().getShowManager().getIdentifiers());
        return suggestions;
    }
}

class MaterialTabArgument extends TabArgument {
    public MaterialTabArgument() { name = "material"; }

    @Override
    public List<String> getSuggestions() {
        for (Material material : Material.values()) {
            suggestions.add(material.name());
        }
        return suggestions;
    }
}

class SchematicTabArgument extends TabArgument {
    public SchematicTabArgument() { name = "schematic"; }

    @Override
    public List<String> getSuggestions() {
        File schematicDir = new File("plugins/WorldEdit/schematics/");
        if (schematicDir.isDirectory()) {
            for (File file : Objects.requireNonNull(schematicDir.listFiles())) {
                if (file.isFile()) { suggestions.add(file.getName()); }
            }
        }
        return suggestions;
    }
}

class PositionalWorldArgument extends TabArgument {
    public PositionalWorldArgument() {
        name = "world";
        personal = true;
    }

    @Override
    public List<String> getSuggestions(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return Collections.singletonList(player.getWorld().getName());
        }
        return suggestions;
    }
}

class PositionalXArgument extends TabArgument {
    public PositionalXArgument() {
        name = "x";
        personal = true;
    }

    @Override
    public List<String> getSuggestions(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return Collections.singletonList(String.valueOf(player.getLocation().getBlockX()));
        }
        return suggestions;
    }
}

class PositionalYArgument extends TabArgument {
    public PositionalYArgument() {
        name = "y";
        personal = true;
    }

    @Override
    public List<String> getSuggestions(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return Collections.singletonList(String.valueOf(player.getLocation().getBlockY()));
        }
        return suggestions;
    }
}

class PositionalZArgument extends TabArgument {
    public PositionalZArgument() {
        name = "z";
        personal = true;
    }

    @Override
    public List<String> getSuggestions(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return Collections.singletonList(String.valueOf(player.getLocation().getBlockZ()));
        }
        return suggestions;
    }
}