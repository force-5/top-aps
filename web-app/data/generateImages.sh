
for ((  i = 0 ;  i <= 15;  i++  ))
do
convert contractor.jpg -gravity North -background YellowGreen  -splice 0x18  -draw "text 0,2 'Contractor - $i'" contractor-$i.jpg
convert certificationLogo.jpg -gravity North -background YellowGreen  -splice 0x18  -draw "text 0,2 'Logo - $i'" certificationLogo-$i.jpg
convert affidavit.jpg -gravity North -background YellowGreen  -splice 0x25  -draw "text 0,2 'Logo - $i'" affidavit-$i.jpg
done
