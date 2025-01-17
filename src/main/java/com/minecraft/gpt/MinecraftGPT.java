package com.minecraft.gpt;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

public class MinecraftGPT extends JavaPlugin {
    private String apiKey;
    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        apiKey = getConfig().getString("openai-api-key", "");
        if (apiKey.isEmpty()) {
            getLogger().warning("OpenAI API key not set! Please set it in config.yml");
        }
        getLogger().info("MinecraftGPT has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MinecraftGPT has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("ask")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Please provide a question!");
                return true;
            }

            if (apiKey.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "OpenAI API key not set! Please ask an admin to set it in the config.");
                return true;
            }

            Player player = (Player) sender;
            String question = String.join(" ", args);
            
            // Send "Thinking..." message
            player.sendMessage(ChatColor.YELLOW + "Thinking...");
            
            // Run API request async
            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    String response = askGPT(question);
                    // Send response on main thread
                    getServer().getScheduler().runTask(this, () -> {
                        player.sendMessage(ChatColor.GREEN + response);
                    });
                } catch (Exception e) {
                    String errorMessage = "Error: ";
                    if (e.getMessage().contains("timeout")) {
                        errorMessage += "Request timed out. Please try again.";
                    } else if (e.getMessage().contains("429")) {
                        errorMessage += "Too many requests. Please wait a moment and try again.";
                    } else {
                        errorMessage += e.getMessage();
                    }
                    
                    final String finalError = errorMessage;
                    getServer().getScheduler().runTask(this, () -> {
                        player.sendMessage(ChatColor.RED + finalError);
                    });
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }

    private String askGPT(String question) throws Exception {
        JSONObject requestBody = new JSONObject()
            .put("model", "gpt-4")
            .put("messages", new JSONArray()
                .put(new JSONObject()
                    .put("role", "system")
                    .put("content", "You are a helpful AI assistant in a Minecraft server. Keep responses concise, relevant to Minecraft, and family-friendly. Provide practical advice and interesting facts about the game."))
                .put(new JSONObject()
                    .put("role", "user")
                    .put("content", question)))
            .put("max_tokens", 250)
            .put("temperature", 0.7);

        Request request = new Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer " + apiKey)
            .post(RequestBody.create(requestBody.toString(), JSON))
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String error = "API request failed: " + response.code();
                if (response.body() != null) {
                    error += " - " + response.body().string();
                }
                throw new Exception(error);
            }
            
            JSONObject jsonResponse = new JSONObject(response.body().string());
            return jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim();
        }
    }
} 