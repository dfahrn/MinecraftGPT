# MinecraftGPT Plugin

![image](https://github.com/user-attachments/assets/bc5e78c4-e18f-4a0a-a07b-1c73715ce502)

![image](https://github.com/user-attachments/assets/d4eb585a-5282-49a0-b21c-78d7993699d9)



A Minecraft plugin that allows players to interact with GPT-4 AI directly in the game using the `/ask` command.

## Features

- Chat with GPT-4 directly in Minecraft
- Family-friendly responses focused on Minecraft
- Async processing to prevent server lag
- Configurable settings
- Color-coded responses

## Requirements

- Spigot/Paper Minecraft server 1.20.x
- Java 17 or higher
- OpenAI API key with GPT-4 access

## Installation

1. Download the latest `MinecraftGPT-1.0-SNAPSHOT.jar` from the releases page
2. Place the JAR file in your server's `plugins` folder
3. Start/restart your server
4. Configure your OpenAI API key in `plugins/MinecraftGPT/config.yml`

## Configuration

The plugin will generate a `config.yml` file in `plugins/MinecraftGPT/` with these settings:

```yaml
# Your OpenAI API key (required)
openai-api-key: ""

# Model settings
model:
  # Maximum length of the response (in tokens)
  max-tokens: 250
  # Response creativity (0.0 to 1.0)
  temperature: 0.7

# Message settings
messages:
  prefix: "&8[&bMinecraftGPT&8]&r "
  thinking: "&eThinking..."
  error: "&cError: %s"
```

## Usage

Use the `/ask` command followed by your question:
```
/ask What is the best way to find diamonds?
```

## Permissions

- `minecraftgpt.ask` - Allows use of the /ask command (default: true)

## Building from Source

1. Clone the repository
2. Install Maven
3. Run `mvn clean install`
4. Find the compiled JAR in `target/MinecraftGPT-1.0-SNAPSHOT.jar`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. 
