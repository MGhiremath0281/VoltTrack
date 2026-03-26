$URL = "http://localhost:8080/api/meter-readings"

$PULSE = 1000
$UNITS = 50.0

while ($true) {

    $TIMESTAMP = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")

    $DELTA_PULSE = Get-Random -Minimum 5 -Maximum 20
    $DELTA_UNITS = Get-Random -Minimum 0.1 -Maximum 0.5

    $PULSE += $DELTA_PULSE
    $UNITS += $DELTA_UNITS

    $VOLTAGE = Get-Random -Minimum 225 -Maximum 235
    $CURRENT = Get-Random -Minimum 3 -Maximum 6

    $body = @{
        meterPublicId = "MTR-bdc24bef"
        pulseCount = $PULSE
        voltage = $VOLTAGE
        current = $CURRENT
        unitsConsumed = $UNITS
        timestamp = $TIMESTAMP
    } | ConvertTo-Json

    try {
        Invoke-RestMethod -Uri $URL -Method Post -Body $body -ContentType "application/json"
        Write-Host "Sent -> Pulse: $PULSE | Units: $UNITS | Time: $TIMESTAMP"
    } catch {
    Write-Host "❌ Error:"
    Write-Host $_.Exception.Message
    if ($_.Exception.Response -ne $null) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body:"
        Write-Host $responseBody
    }
}
    Start-Sleep -Seconds 60
}