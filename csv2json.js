const fs = require('fs');
const filePath = process.argv[2];

try {
    const content = fs.readFileSync(filePath, 'utf8');

    const lines = content.split("\n");

    const items = [];

    lines.forEach( line => {
        const parts = line.split(",");

        const name = parts[0].trim();
        const requiredCount = parts[1].trim();
        const stock = parts[2].trim();
        const homeLocation = parts[4].trim();
        const marketLocation = parts[5].trim();

        const item = {name, requiredCount, stock, homeLocation, marketLocation}
        items.push(item);
    });

    console.log(JSON.stringify(items));

} catch (err) {
    console.error('Error reading file:', err);
}