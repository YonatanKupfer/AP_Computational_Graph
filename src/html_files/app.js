function updateTable(data) {
    const tableBody = document.querySelector('#topicTable tbody');
    tableBody.innerHTML = '';
    for (const [topic, message] of Object.entries(data.topics)) {
        const row = tableBody.insertRow();
        const topicCell = row.insertCell(0);
        const messageCell = row.insertCell(1);
        topicCell.textContent = topic;
        messageCell.textContent = message;
    }
}

function updateGraph(data) {
    const graphFrame = parent.document.getElementById('graphFrame');
    graphFrame.contentWindow.postMessage({ type: 'updateNodes', data: data.topics }, '*');
}

function showRefreshIndicator() {
    const indicator = document.getElementById('refreshIndicator');
    if (indicator) {
        indicator.style.display = 'block';
    }
}

function hideRefreshIndicator() {
    const indicator = document.getElementById('refreshIndicator');
    if (indicator) {
        indicator.style.display = 'none';
    }
}

function fetchData() {
    showRefreshIndicator();
    fetch('/publish') // Ensure the URL is correct
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Fetched data:', data);  // Add this line
            updateTable(data);
            updateGraph(data);
            hideRefreshIndicator();
        })
        .catch(error => {
            console.error('Error fetching data:', error);
            hideRefreshIndicator();
        });
}

// Fetch data every 5 seconds
// setInterval(fetchData, 5000);

// Initial fetch
fetchData();
