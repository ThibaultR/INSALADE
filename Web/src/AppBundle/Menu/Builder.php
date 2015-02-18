<?php
namespace AppBundle\Menu;

use Knp\Menu\FactoryInterface;
use Symfony\Component\DependencyInjection\ContainerAware;

class Builder extends ContainerAware
{
    public function mainMenu(FactoryInterface $factory, array $options)
    {
        $menu = $factory->createItem('root', array(
            'navbar' => true,
            'pull-right' => true,
        ));

        if($this->container->get('security.context')->isGranted("ROLE_USER"))
        {
            $home = $menu->addChild('Logout', array(
                'route' => 'fos_user_security_logout',
            ));
        }
        else {
            $register = $menu->addChild('Register', array(
                'route' => 'amicale_registration'
            ));

            $login = $menu->addChild('Login', array(
                'route' => 'fos_user_security_login'
            ));
        }

        return $menu;
    }
}