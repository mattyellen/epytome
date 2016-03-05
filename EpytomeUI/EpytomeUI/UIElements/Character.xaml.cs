using System.Windows.Controls;

namespace EpytomeUI.UIElements
{
    public partial class Character : UserControl
    {
        public Character()
        {
            InitializeComponent();
        }

        public void SetHeight(double height)
        {
            _characterScaleTransform.ScaleX = height / 64;
            _characterScaleTransform.ScaleY = height / 64;
        }
    }

}
